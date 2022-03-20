package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String currentUser;
    private TextView stickersSent;
    private RecyclerView recyclerView;
    private HistoryRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<HistoryActivity.HistoryItem> historyItems = new ArrayList<>();

    public static class HistoryItem {
        private final int stickerSource;
        private final String stickerSender;
        private final String stickerTime;

        public HistoryItem(int stickerSource, String stickerSender, String stickerTime) {
            this.stickerSource = stickerSource;
            this.stickerSender = stickerSender;
            this.stickerTime = stickerTime;
        }

        public int getstickerSource() {
            return stickerSource;
        }

        public String getstickerSender() {
            return stickerSender;
        }
        public String getstickerTime() {
            return stickerTime;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        stickersSent = findViewById(R.id.stickersSent);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("CANNOT GET TOKEN", "no token");
                Toast toast = Toast.makeText(HistoryActivity.this, "Cannot get token", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    currentUser = b.getString("currentUser");
                }
                mDatabase.child("users").child(currentUser).get().addOnCompleteListener(t -> {
                    if (!t.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("Stickers sent", String.valueOf(t.getResult().child("stickersSent").getValue()));
                        String stickersSentText = "Stickers Sent: " + t.getResult().child("stickersSent").getValue();
                        stickersSent.setText(stickersSentText);
                        stickersSent.setTextSize(20);
                        for (DataSnapshot dschild : t.getResult().getChildren()) {
                            if (dschild.hasChildren()) {
                                // It's a Sticker Node, need to parse the data
                                int stickerID = Integer.parseInt(String.valueOf(dschild.child("stickerID").getValue()));
                                String senderName = "Sent By: " + dschild.child("senderName").getValue();
                                String timeSent = String.valueOf(dschild.child("timeSent").getValue());

                                Log.d("Sticker path", String.valueOf(stickerID));
                                Log.d("Sticker sender", senderName);
                                Log.d("Sticker time", timeSent);
                                addHistoryItemToRecyclerView(stickerID, senderName, timeSent);
                            }
                        }
                    }
                });
            }
        });
        generateRecyclerView();
    }

        private void addHistoryItemToRecyclerView(Integer stickerID, String senderName, String timeSent) {
            recyclerViewLayoutManager.smoothScrollToPosition(recyclerView, null, 0);
            historyItems.add(0, new HistoryActivity.HistoryItem(stickerID, senderName, timeSent));
            recyclerViewAdapter.notifyItemInserted(0);
        }

        private void generateRecyclerView() {
            recyclerViewLayoutManager = new LinearLayoutManager(this);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            recyclerViewAdapter = new HistoryRecyclerViewAdapter(historyItems);

            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setLayoutManager(recyclerViewLayoutManager);

        }

}