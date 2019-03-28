package com.example.tiktalk.Listeners;

public interface MyChildListener<A,C,E> {
    void onChildAdded(A childAdded);

    void onChildChanged(C childChanged);

    void onCancelled(E databaseError);
}
