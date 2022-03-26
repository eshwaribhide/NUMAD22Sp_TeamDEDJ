package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

// store name of user locally so that can access the token later
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
        initSavedInstanceState(savedInstanceState);
        Log.e("IN ON CREATE", "STICKER MESSAGING");
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast toast = Toast.makeText(StickerMessagingActivity.this, "Cannot get token", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Log.e("CLIENT_REGISTRATION_TOKEN", task.getResult());
                mDatabase = FirebaseDatabase.getInstance().getReference();
                 //FOR TESTING
//                mDatabase.child("currentUser").get().addOnCompleteListener(t1 -> {
//                    if (!t1.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", t1.getException());
//                    } else {
                       // currentUser = String.valueOf(t1.getResult().getValue());
                        // create the notification channel while currentUser is found
                        createNotificationChannel();
                        mDatabase.child("users").child(currentUser).get().addOnCompleteListener(t2 -> {
                            if (t2.getResult().getValue() == null) {
                                mDatabase.child("users").child(currentUser).setValue(new User(currentUser, task.getResult()));
                            }
                            else {
                                mDatabase.child("users").child(currentUser).child("clientRegistrationToken").setValue(task.getResult());
                            }
                            Spinner destUsersDropdown = findViewById(R.id.destUsers);

                            // Get all users from the database and add to the dropdown
                            mDatabase.child("users").get().addOnCompleteListener(t3 -> {
                                if (!t3.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", t3.getException());
                                } else {
                                    // need to save this when going  back if new users added
                                    ArrayList<String> destUsers = new ArrayList<>();
                                    for (DataSnapshot dschild : t3.getResult().getChildren()) {
                                        destUsers.add(String.valueOf(dschild.getKey()));
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, destUsers);
                                    destUsersDropdown.setAdapter(adapter);
                                }
                            });

                        });

            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.e("HERE", "HERE");
        Log.e("current user", currentUser);

        outState.putString("currentUser", currentUser);
        Log.e("HASKEY", String.valueOf(outState.containsKey("currentUser")));
        super.onSaveInstanceState(outState);
    }

    private void initData(Bundle savedInstanceState) {
        Log.e("INITDATA", "IN FUNCTION");
        Log.e("SAVEDINSTANCESTATE", String.valueOf(savedInstanceState));
        if (savedInstanceState != null && savedInstanceState.containsKey("currentUser")) {
            Log.e("GETTINGSTRING", "GETTINGSTRING");
            currentUser = savedInstanceState.getString("currentUser");
        }
        else {
            //Log.e("CONTAINSKEY", String.valueOf(savedInstanceState.containsKey("currentUser")));
            Bundle b = getIntent().getExtras();
            if (b != null) {
                currentUser = b.getString("currentUser");
            }
        }
    }

    private void initSavedInstanceState(Bundle savedInstanceState) {
        initData(savedInstanceState);
    }

    // send a sticker from the device to the selected user
    public void sendStickerMessage(View view) {
        Log.e("in sendStickerMessage", "in sendStickerMessage function");
        Spinner destUsersDropdown = findViewById(R.id.destUsers);
        String destUser = destUsersDropdown.getSelectedItem().toString();
        Log.e(TAG, destUser);
        mDatabase.child("users").child(destUser).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.e("Before sent sticker", "in sendStickerMessage");
                // get token of the user from the database
                String clientRegistrationToken = String.valueOf(task.getResult().child("clientRegistrationToken").getValue());

                int stickerId = view.getId();
                int sentSticker;
                String countChildValue;
                if (stickerId == R.id.helloSticker) {
                    sentSticker = R.drawable.hello;
                    Log.e("Sent sticker", Integer.toString(sentSticker));
                    countChildValue = "helloStickerCount";
                } else if (stickerId == R.id.presentSticker) {
                    sentSticker = R.drawable.presents;
                    Log.e("Sent sticker", Integer.toString(sentSticker));
                    countChildValue = "presentStickerCount";
                } else if (stickerId == R.id.laughSticker) {
                    sentSticker = R.drawable.laugh;
                    Log.e("Sent sticker", Integer.toString(sentSticker));
                    countChildValue = "laughStickerCount";
                } else {
                    sentSticker = R.drawable.burger;
                    Log.e("Sent sticker", Integer.toString(sentSticker));
                    countChildValue = "burgerStickerCount";
                }
                new Thread(() -> sendStickerMessage(destUser, clientRegistrationToken, sentSticker)).start();
                // update number of stickers sent by this user
                StickerMessagingActivity.this.updateStickersSent(countChildValue);

            }
        });
    }

    private void sendStickerMessage(String destUser, String targetToken, int sentSticker) {
        mDatabase.child("users").child(destUser).child(new Date().toString()).setValue(new Sticker(sentSticker, currentUser, new Date().toString()));

        // This has to do with notifications
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Sticker Received");
            jNotification.put("body", "View Sticker");
            jNotification.put("badge", "1");
            // Just a temporary image for now just to fulfill requirement of "not only text image"
            jNotification.put("image", "https://i.imgur.com/Or7eeA9.jpg");


            jData.put("title", "Sticker");
            // hardcoded for now, but actual content will be image tapped on by user
            // will have to be handled in on click for the image
            jData.put("content", sentSticker);

            // /topics/user will be the topic to subscribe to for currentUser
            jPayload.put("to", targetToken);

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

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

    public void historyButtonOnClick(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
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

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), currentUser, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            subscribeToStickerNotifications();
        }
    }

    public void subscribeToStickerNotifications(){
        // subscribe to incoming stickers for currentUser
        FirebaseMessaging.getInstance().subscribeToTopic(currentUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = currentUser + "is subscribed to notifications";
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe to " + currentUser + "'s notifications";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(StickerMessagingActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    currentUser = b.getString("currentUser");
                }
            }
        }
    }
}