package edu.neu.madcourse.numad22sp_teamdedj;

import java.util.HashMap;

public class User {
    public String username;
    public String clientRegistrationToken;
    // needed for history
    public Integer stickersSent;
    // Will map username:Sticker for history
    public HashMap<String,Sticker> stickersReceived;

    public User(String username, String clientRegistrationToken) {
        this.username = username;
        this.clientRegistrationToken = clientRegistrationToken;
        this.stickersSent = 0;
        this.stickersReceived = new HashMap<>();
    }
}
