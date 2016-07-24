package com.artur.dualpair.android.dto;

import java.io.Serializable;

public class Match implements Serializable {

    private User user;
    private User opponent;

    public User getUser() {
        return user;
    }

    public User getOpponent() {
        return opponent;
    }
}
