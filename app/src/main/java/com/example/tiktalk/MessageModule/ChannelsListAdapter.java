package com.example.tiktalk.MessageModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiktalk.Model.User;
import com.example.tiktalk.R;
import com.example.tiktalk.SendBird.TypingIndicator;
import com.example.tiktalk.Utils.DateUtils;
import com.example.tiktalk.Utils.PreferenceUtils;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.UserMessage;

import java.util.ArrayList;
import java.util.List;

public class ChannelsListAdapter extends BaseAdapter {

    public List<GroupChannel> arrayList;
    public Context context;
    public TextView msg_count, Name, lastMessageText, time;
    public ImageView user_msg_pimage, online_dot;
    User user;
    LinearLayout typingIndicatorContainer;


    public ChannelsListAdapter(List<GroupChannel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.user = user;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.messageview, null, false);
        msg_count = (TextView) v.findViewById(R.id.msg_count);
        Name = (TextView) v.findViewById(R.id.Name);
        lastMessageText = (TextView) v.findViewById(R.id.lastMessage);
        time = (TextView) v.findViewById(R.id.time);
        user_msg_pimage = (ImageView) v.findViewById(R.id.user_msg_pimage);
        online_dot = (ImageView) v.findViewById(R.id.online_dot);
        typingIndicatorContainer = (LinearLayout) v.findViewById(R.id.container_group_channel_list_typing_indicator);

        GroupChannel groupChannel = arrayList.get(i);

        int unreadCount = groupChannel.getUnreadMessageCount();
        // If there are no unread messages, hide the unread count badge.
        if (unreadCount == 0) {
            msg_count.setVisibility(View.INVISIBLE);
        } else {
            msg_count.setVisibility(View.VISIBLE);
            msg_count.setText(String.valueOf(groupChannel.getUnreadMessageCount()));
        }

        BaseMessage lastMessage = groupChannel.getLastMessage();
        if (lastMessage != null) {
            time.setVisibility(View.VISIBLE);
            lastMessageText.setVisibility(View.VISIBLE);

            // Display information about the most recently sent message in the channel.
            time.setText(String.valueOf(DateUtils.formatDateTime(lastMessage.getCreatedAt())));

            // Bind last message text according to the type of message. Specifically, if
            // the last message is a File Message, there must be special formatting.
            if (lastMessage instanceof UserMessage) {
                lastMessageText.setText(((UserMessage) lastMessage).getMessage());
            } else if (lastMessage instanceof AdminMessage) {
                lastMessageText.setText(((AdminMessage) lastMessage).getMessage());
            } else {
                String lastMessageString = String.format(
                        context.getString(R.string.group_channel_list_file_message_text),
                        ((FileMessage) lastMessage).getSender().getNickname());
                lastMessageText.setText(lastMessageString);
            }
        } else {
            time.setVisibility(View.INVISIBLE);
            lastMessageText.setVisibility(View.INVISIBLE);
        }

        List<Member> members = groupChannel.getMembers();
        for (Member member : members) {
            if (!PreferenceUtils.getId(context).matches(member.getUserId())){

                if (member.getConnectionStatus().toString().equals("ONLINE")){
                    Glide.with(context).load(R.drawable.online_icon).into(online_dot);
                }

                if (member.getConnectionStatus().toString().equals("OFFLINE")){
                    Glide.with(context).load(R.drawable.online_icon_new).into(online_dot);
                }

//                Glide.with(context).load(R.drawable.online_icon).into(online_dot);
                Glide.with(context).load(member.getProfileUrl()).into(user_msg_pimage);
                Name.setText(member.getNickname());
            }
        }
        /*
         * Set up the typing indicator.
         * A typing indicator is basically just three dots contained within the layout
         * that animates. The animation is implemented in the {@link TypingIndicator#animate() class}
         */
        ArrayList<ImageView> indicatorImages = new ArrayList<>();
        indicatorImages.add((ImageView) v.findViewById(R.id.typing_indicator_dot_1));
        indicatorImages.add((ImageView) v.findViewById(R.id.typing_indicator_dot_2));
        indicatorImages.add((ImageView) v.findViewById(R.id.typing_indicator_dot_3));

        TypingIndicator indicator = new TypingIndicator(indicatorImages, 600);
        indicator.animate();

//         debug
//            typingIndicatorContainer.setVisibility(View.VISIBLE);
//            lastMessageText.setText(("Someone is typing"));

//         If someone in the channel is typing, display the typing indicator.
        if (groupChannel.isTyping()) {
            typingIndicatorContainer.setVisibility(View.VISIBLE);
            lastMessageText.setText(("Someone is typing"));
        } else {
            // Display typing indicator only when someone is typing
            typingIndicatorContainer.setVisibility(View.GONE);
        }


        return v;
    }
}

