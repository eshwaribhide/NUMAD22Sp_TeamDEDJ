package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

// Current issues
// 1. user should only be created if does not exist, right now gets overwritten, perhaps add some ChildEventListeners
// 2. need to add more stickers
// 3. need to add on click functionality to a sticker to make tapping on it the way to send
// 4. Also should only render sticker if exists, if path does not exist then have to have some message/error sticker
// 5. then everything needs to be designed properly (e.g. if we have multiple stickers, maybe a slideshow type thing? Because we can’t tap on the sticker since tapping sends it. Or just have like 2 stickers or something and then lay them out.)
// 6. foreground notifications do not work and also banner notification does not work for some reason
public class StickerMessagingActivity extends AppCompatActivity {

    private static final String TAG = "StickerMessagingActivity";

    private DatabaseReference mDatabase;

    private String currentUser;

    private static final String SERVER_KEY="key=AAAAP4z9QU0:APA91bECheSrt__KSX5dPa-DfGEfb_fWzgi3_E38lvWsyyHenK9F05Uqfo4bjPXhjKjQCXBt5CgtvpC09PQ4c4oZDaHC8ZLHRTBXveiLzQQ5YWDFg9t3Qfod4AKGVMccnQTzxMaQhFWV";

    // For testing purposes, user 2's value
    //private static String CLIENT_REGISTRATION_TOKEN="fCcDoyEjRrGQCrlNj-jCqM:APA91bFMHTOLwz8Bjr585DR65iMaK8p3pjdeKaRdlxJBvgyaOHdQSPhpkcKG27msaTtj1ysW6f64fDXqxRY_qHJiE-qyM_IdTuAIqexmDHpCLEDpGRIQEmXoQna1rwtpk5b3F7pDCOiD";

    // For testing purposes, user 1's value
    //private static String CLIENT_REGISTRATION_TOKEN="f9BCIWKoQT2TAoGlb_Evtk:APA91bFT6XMVzCXiLyNd5XRzCd8fXtz37t6bVXnSCdgC7Bjxl80ayLLUWwMcMDn5_OI9Yh38pOhDbE9jEHRBBqDFKXgFIvdQxeyft2z77EqdTcbV8VHqIyc_X4hsNtVEApCvXsUiAsF6";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_messaging);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast toast = Toast.makeText(StickerMessagingActivity.this, "Cannot get token", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.e("CLIENT_REGISTRATION_TOKEN", task.getResult());
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    currentUser = b.getString("currentUser");
                }
                 //FOR TESTING
                // I assume this is why the history page always shows 8 stickers sent
                 currentUser="user2";
                // Need to only set this if the current user does not exist, perhaps add some ChildEventListeners
                // mDatabase.child("users").child(currentUser).setValue(new User(currentUser, task.getResult()));
                // Log.e(TAG, "CREATED USER");

                Spinner destUsersDropdown = findViewById(R.id.destUsers);

                // Get all users from the database and add to the dropdown
                mDatabase.child("users").get().addOnCompleteListener(t -> {
                    if (!t.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        ArrayList<String> destUsers = new ArrayList<>();
                        for (DataSnapshot dschild : t.getResult().getChildren()) {
                            destUsers.add(String.valueOf(dschild.getKey()));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, destUsers);
                        destUsersDropdown.setAdapter(adapter);
                    }
                });
            }
        });
    }

    // send a sticker from the device to the selected user
    public void sendStickerMessage(View view) {
        Spinner destUsersDropdown = findViewById(R.id.destUsers);
        String destUser = destUsersDropdown.getSelectedItem().toString();
        Log.e(TAG, destUser);
        mDatabase.child("users").child(destUser).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                // get token of the user from the database
                String clientRegistrationToken = String.valueOf(task.getResult().child("clientRegistrationToken").getValue());
                // send the message
                int stickerId = view.getId();
                int sentSticker;
                String countChildValue;
                if (stickerId == R.id.helloSticker) {
                    sentSticker = R.drawable.hello;
                    countChildValue = "helloStickerCount";
                } else if (stickerId == R.id.presentSticker) {
                    sentSticker = R.drawable.presents;
                    countChildValue = "presentStickerCount";
                } else if (stickerId == R.id.laughSticker) {
                    sentSticker = R.drawable.laugh_sticker;
                    countChildValue = "laughStickerCount";
                } else {
                    sentSticker = R.drawable.burger_sticker;
                    countChildValue = "burgerStickerCount";
                }
                new Thread(() -> sendStickerMessage(destUser, clientRegistrationToken, sentSticker)).start();
                // update number of stickers sent by this user
                StickerMessagingActivity.this.updateStickersSent(countChildValue);
            }
        });
    }

    private void sendStickerMessage(String destUser, String targetToken, int sentSticker) {
        // Need to replace static image with chosen image
        // This will write to the database in order to have history

        // Get the correct sticker image

        System.out.println(sentSticker);
        mDatabase.child("users").child(destUser).child(new Date().toString()).setValue(new Sticker(sentSticker, currentUser, new Date().toString()));

        // This has to do with notifications
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Sticker Received");
            jNotification.put("body", "View Sticker");
            jNotification.put("badge", "1");
            // Just a temporary image for now just to fulfill requirement of "not only text image"
            jNotification.put("image", "https://i.imgur.com/Or7eeA9.jpg");


            jdata.put("title", "Sticker");
            // hardcoded for now, but actual content will be image tapped on by user
            // will have to be handled in on click for the image
            jdata.put("content", sentSticker);

            jPayload.put("to", targetToken);

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jdata);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String resp = "NULL";
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.setRequestMethod("POST");
            req.setRequestProperty("Content-Type", "application/json");
            req.setRequestProperty("Authorization", SERVER_KEY);
            req.setDoOutput(true);

            OutputStream outputStream = req.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            Scanner s = new Scanner(req.getInputStream()).useDelimiter("\\A");
            resp = s.hasNext() ? s.next() : "";

        } catch (IOException e) {
            Log.e(TAG, "IO Exception in sending message");
        }

        //postToastMessage("Status from Server: " + resp, getApplicationContext());

    }

    private void updateStickersSent(String countChildValue) {
        mDatabase.child("users").child(currentUser).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                int stickersSent = Integer.parseInt(String.valueOf(task.getResult().child(countChildValue).getValue()));
                int newStickersSent = stickersSent + 1;
                mDatabase.child("users").child(currentUser).child(countChildValue).setValue(newStickersSent);
            }
        });
    }

//    // Just called at the beginning once message is finished being sent. Can be deleted later.
//    public static void postToastMessage(final String message, final Context context){
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
//    }

    public void stickersReceivedButtonOnClick(View view) {
        Intent intent = new Intent(this, StickersReceivedActivity.class);
        // Enclose the currentUser information as a parameter
        Bundle b = new Bundle();
        b.putString("currentUser", currentUser);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void stickersSentButtonOnClick(View view) {
        Intent intent = new Intent(this, StickersSentActivity.class);
        // Enclose the currentUser information as a parameter
        Bundle b = new Bundle();
        b.putString("currentUser", currentUser);
        intent.putExtras(b);
        startActivity(intent);
    }

}