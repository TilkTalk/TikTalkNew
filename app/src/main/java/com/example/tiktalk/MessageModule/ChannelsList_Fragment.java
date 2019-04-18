package com.example.tiktalk.MessageModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tiktalk.BaseClasses.BaseFragment;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.ConnectionManager;
import com.example.tiktalk.SendBird.SendbirdChatActivity;
import com.example.tiktalk.Utils.AppUtils;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.List;

public class ChannelsList_Fragment extends BaseFragment {

    String member_name,member_pic,memberid;
    Button menu_btn;
    public DrawerLayout drawer_layout;
    String type, status, id, username, imageUrl, rating, coinPerMin, rateperMin, about;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getArguments();
        type = bundle.getString("type");
        id = bundle.getString("myId");
        username = bundle.getString("myName");
        imageUrl = bundle.getString("myImage");
        rating = bundle.getString("myRating");
        coinPerMin = bundle.getString("coinPerMin");
        rateperMin = bundle.getString("$PerMin");
        about = bundle.getString("about");
//        if (bundle.containsKey("type")) {
//            type = bundle.getString("seller");
//        }
//        else {
//            type = bundle.getString("buyer");
//        }
        }

    public ListView chat_user_list;
    public ChannelsListAdapter channelsListAdapter;
    private List<GroupChannel> groupChannerls;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channels_list_frag, null);
        return view;

    }

    @Override
    public void initializeComponents(View rootView) {
        chat_user_list = (ListView) view.findViewById(R.id.chat_user_list);
        GroupChannelListQuery filteredQuery = GroupChannel.createMyGroupChannelListQuery();
        List<String> userIds = new ArrayList<>();
        userIds.add(PreferenceUtils.getId(getActivity()));

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.show();
        // Returns channelA and channelB that include { John }, plus channelA and channelB that include { Jay }, plus channelB that include { Jin }.
        // Actually channelA and channelB are returned.
        filteredQuery.setUserIdsIncludeFilter(userIds, GroupChannelListQuery.QueryType.OR);
        filteredQuery.next((new GroupChannelListQuery.GroupChannelListQueryResultHandler() {
            @Override
            public void onResult(List<GroupChannel> list, SendBirdException e) {
                dialog.dismiss();
                if (e != null) {// Error.
//                    AppUtils.Toast(e.getMessage());
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    groupChannerls = list;
                    channelsListAdapter = new ChannelsListAdapter(list, getActivity());
                    if (chat_user_list != null)
                        chat_user_list.setAdapter(channelsListAdapter);
                }
            }
        }));



        chat_user_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                final Intent chatIntent = new Intent(getActivity(), SendbirdChatActivity.class);
                GroupChannel groupChannel = groupChannerls.get(pos);
                List<Member> members = groupChannel.getMembers();
                for (Member member : members) {
                    if (!PreferenceUtils.getId(getActivity()).matches(member.getUserId())) {
                        member_name = member.getNickname();
                        member_pic = member.getProfileUrl();
                        memberid=member.getUserId();

                        if (member.getConnectionStatus().toString().equals("ONLINE")){
                            status = "online";
                        }
                        else {
                            status = "offline";
                        }
                    }
                }
                chatIntent.putExtra("title", member_name);
                chatIntent.putExtra("channelUrl", groupChannel.getUrl());
                chatIntent.putExtra("cover", member_pic);
                chatIntent.putExtra("members", memberid);
                chatIntent.putExtra("type", type);
                chatIntent.putExtra("status", status);

                chatIntent.putExtra("myId", id);
                chatIntent.putExtra("myName", username);
                chatIntent.putExtra("myImage", imageUrl);
                chatIntent.putExtra("myRating", rating);
                chatIntent.putExtra("coinPerMin", coinPerMin);
                chatIntent.putExtra("$PerMin", rateperMin);
                chatIntent.putExtra("about", about);
                startActivity(chatIntent);
            }
        });
    }

    @Override
    public void setupListeners(View rootView) {

    }

    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHANNEL_LIST";
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_LIST";
    @Override
    public void onResume() {

        ConnectionManager.addConnectionManagementHandler(CONNECTION_HANDLER_ID, new ConnectionManager.ConnectionManagementHandler() {
            @Override
            public void onConnected(boolean reconnect) {
                initializeComponents(view);//update list when
            }
        });

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
            }

            @Override
            public void onChannelChanged(BaseChannel channel) {
                initializeComponents(view);
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
//                mChannelListAdapter.notifyDataSetChanged();
            }
        });

        super.onResume();
    }

    @Override
    public void onPause() {
        ConnectionManager.removeConnectionManagementHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }
}

