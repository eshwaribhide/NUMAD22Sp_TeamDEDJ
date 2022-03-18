package edu.neu.madcourse.numad22sp_teamdedj;

import java.util.HashMap;

public class User {
    public String username;
    public String clientRegistrationToken;
    // needed for history
    public Integer stickersSent;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String clientRegistrationToken) {
        this.username = username;
        this.clientRegistrationToken = clientRegistrationToken;
        this.stickersSent = 0;
    }
}
