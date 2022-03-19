package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("CANNOT GET TOKEN", "no token");
                        Toast toast = Toast.makeText(HistoryActivity.this, "Cannot get token", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Log.e("CLIENT_REGISTRATION_TOKEN", task.getResult());
                        Log.e("DATABASE", String.valueOf(mDatabase));
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        Log.e("DATABASE", "db");
                        Log.e("DATABASE", String.valueOf(mDatabase));
                        Bundle b = getIntent().getExtras();
                        if (b != null) {
                            currentUser = b.getString("currentUser");
                            Log.e("CURRENT USER", currentUser);
                        }
                        Log.e("DATABASE", String.valueOf(mDatabase.child("users").child("user2")));
                        mDatabase.child("users").child("user2").get().addOnCompleteListener(t -> {
                            if (!t.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                Log.d("Stickers sent", String.valueOf(t.getResult().child("stickersSent").getValue()));
                                for (DataSnapshot dschild : t.getResult().getChildren()) {
                                    if (dschild.hasChildren()) {
                                        // It's a Sticker Node, need to parse the data
                                        Log.d("Sticker path", String.valueOf(dschild.child("stickerPath").getValue()));
                                        Log.d("Sticker sender", String.valueOf(dschild.child("senderName").getValue()));
                                        Log.d("Sticker time", String.valueOf(dschild.child("timeSent").getValue()));
                                    }
                                }
                            }
                        });
                    }
                });

//                 //remove hardcoded reference to user2, change instead to current user
//                mDatabase.child("users").child("user2").get().addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    } else {
//                        Log.d("Stickers sent", String.valueOf(task.getResult().child("stickersSent").getValue()));
//                        for (DataSnapshot dschild : task.getResult().getChildren()) {
//                            if(dschild.hasChildren()) {
//                                // It's a Sticker Node, need to parse the data
//                                Log.d("Sticker path", String.valueOf(dschild.child("stickerPath").getValue()));
//                                Log.d("Sticker sender", String.valueOf(dschild.child("senderName").getValue()));
//                                Log.d("Sticker time", String.valueOf(dschild.child("timeSent").getValue()));
//                            }
//                        }
//                    }
//                });

    }

}