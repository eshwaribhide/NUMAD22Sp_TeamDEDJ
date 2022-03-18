package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

// Current issues
// 1. user should only be created if does not exist, right now gets overwritten, perhaps add some ChildEventListeners
// 2. need ability to choose user whom to send to (either type username, or
// a dropdown of all the current users in the database and then select)
// 3. need to add more stickers
// 4. need to add on click functionality to a sticker to make tapping on it the way to send
// 5. Also should only render sticker if exists, if path does not exist then have to have some message/error sticker
// 5. then everything needs to be designed properly (e.g. if we have multiple stickers, maybe a slideshow type thing? Because we can’t tap on the sticker since tapping sends it. Or just have like 2 stickers or something and then lay them out.)
// 6. foreground notifications do not work and also banner notification does not work for some reason
public class StickerMessagingActivity extends AppCompatActivity {

    private static final String TAG = "StickerMessagingActivity";

    private DatabaseReference mDatabase;

    private String currentUser;

    private static final String SERVER_KEY="key=AAAAP4z9QU0:APA91bECheSrt__KSX5dPa-DfGEfb_fWzgi3_E38lvWsyyHenK9F05Uqfo4bjPXhjKjQCXBt5CgtvpC09PQ4c4oZDaHC8ZLHRTBXveiLzQQ5YWDFg9t3Qfod4AKGVMccnQTzxMaQhFWV";

    // For testing purposes
    private static String CLIENT_REGISTRATION_TOKEN="fCcDoyEjRrGQCrlNj-jCqM:APA91bFMHTOLwz8Bjr585DR65iMaK8p3pjdeKaRdlxJBvgyaOHdQSPhpkcKG27msaTtj1ysW6f64fDXqxRY_qHJiE-qyM_IdTuAIqexmDHpCLEDpGRIQEmXoQna1rwtpk5b3F7pDCOiD";


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

                /////////////////This dialog is for login/////////////////
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Login");

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText editUsername = new EditText(this);
                editUsername.setHint("Enter Username");
                layout.addView(editUsername);

                editUsername.setTextColor(Color.parseColor("#9C27B0"));

                alertDialogBuilder.setView(layout);
                alertDialogBuilder.setPositiveButton("OK", (dialog, whichButton) -> {
                    String username = editUsername.getText().toString();
                    currentUser=username;
                    // Value of task.getResult() is the client registration token
                    // Need to only set this if the current user does not exist, perhaps add some ChildEventListeners
                    mDatabase.child("users").child(currentUser).setValue(new User(currentUser, task.getResult()));
                    Log.e(TAG, "CREATED USER");

                });
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

            }
        });

    }

    public void sendStickerMessage(View view) {
        // send the message
        new Thread(() -> sendStickerMessage(CLIENT_REGISTRATION_TOKEN)).start();
        // update number of stickers sent by this user
        StickerMessagingActivity.this.onUpdateStickersSent(mDatabase, currentUser);
    }

    private void sendStickerMessage(String targetToken) {
        // for now just hardcoded destination user to user2
        // but once it is dynamic, then will need to read from db and get the client registration token for the destination user
        mDatabase.child("users").child("user2").child(new Date().toString()).setValue(new Sticker("R.drawable.presents", currentUser, new Date().toString()));

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
            jdata.put("content", "R.drawable.presents");

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

        postToastMessage("Status from Server: " + resp, getApplicationContext());

    }

    private void onUpdateStickersSent(DatabaseReference postRef, String user) {
        Log.e(TAG, "In onupdatestickerssent");
        postRef
                .child("users")
                .child(user)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        User user = mutableData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(mutableData);
                        }

                        user.stickersSent = user.stickersSent + 1;

                        mutableData.setValue(user);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onUpdateStickersSent:" + databaseError);
                        if (databaseError != null) {
                            Toast.makeText(getApplicationContext()
                                    , "Database Error: " + databaseError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Just called at the beginning once message is finished being sent. Can be deleted later.
    public static void postToastMessage(final String message, final Context context){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    public void historyButtonOnClick(View view) {
        // actually should just create an intent and go to new activity
        // then in the new activity make the db reference same and then get info
        // remove hardcoded reference to user2, change instead to current user
        mDatabase.child("users").child("user2").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                    Log.d("Stickers sent", String.valueOf(task.getResult().child("stickersSent").getValue()));
                    for (DataSnapshot dschild : task.getResult().getChildren()) {
                        if(dschild.hasChildren()) {
                            // It's a Sticker Node, need to parse the data
                            Log.d("Sticker path", String.valueOf(dschild.child("stickerPath").getValue()));
                            Log.d("Sticker sender", String.valueOf(dschild.child("senderName").getValue()));
                            Log.d("Sticker time", String.valueOf(dschild.child("timeSent").getValue()));
                        }
                    }
                }
        });
    }

}