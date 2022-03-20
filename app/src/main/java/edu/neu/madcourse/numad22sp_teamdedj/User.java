package edu.neu.madcourse.numad22sp_teamdedj;

public class User {
    public String username;
    public String clientRegistrationToken;
    public int presentStickerCount = 0;
    public int burgerStickerCount = 0;
    public int laughStickerCount = 0;
    public int helloStickerCount = 0;



    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String clientRegistrationToken) {
        this.username = username;
        this.clientRegistrationToken = clientRegistrationToken;
    }
}
