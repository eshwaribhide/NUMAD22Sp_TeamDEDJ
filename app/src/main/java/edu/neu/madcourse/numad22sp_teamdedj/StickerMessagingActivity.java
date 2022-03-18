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

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


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
                    // Value of task.getResult() is the client registration token
                    mDatabase.child("users").child(username).setValue(new User(username, task.getResult()));
                    Log.e(TAG, "CREATED USER");
                    currentUser=username;
                });
                alertDialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });

    }

    public void sendStickerMessage(View view) {
        Task t1 = mDatabase.child("users").child("to-user").child(date()).setValue(new Sticker("R.drawable.presents", currentUser, date()));


        //new Thread(() -> sendStickerMessage(CLIENT_REGISTRATION_TOKEN)).start();
    }

    private void sendStickerMessage(String targetToken) {

        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Sticker Received");
            jNotification.put("body", "View Sticker");
            jNotification.put("badge", "1");
            // Just a temporary image for now
            jNotification.put("image", "https://i.imgur.com/Or7eeA9.jpg");


            jdata.put("title", "Sticker");
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

    public static void postToastMessage(final String message, final Context context){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
    }

    public static String date() {
        Date dNow = new Date();

        return dNow.toString();
    }

    
}