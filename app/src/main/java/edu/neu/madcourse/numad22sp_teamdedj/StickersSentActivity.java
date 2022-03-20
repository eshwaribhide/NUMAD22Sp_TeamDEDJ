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

public class StickersSentActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String currentUser;
    private TextView StickersSentLabel;
    private RecyclerView recyclerView;
    private StickersSentRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<StickersSent> StickersSent = new ArrayList<>();

    public static class StickersSent {
        private final int stickerSource;
        private final int stickerCount;

        public StickersSent(int stickerSource, int stickerCount) {
            this.stickerSource = stickerSource;
            this.stickerCount = stickerCount;
        }

        public int getstickerSource() {
            return stickerSource;
        }

        public int getstickerCount() {
            return stickerCount;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers_received);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("CANNOT GET TOKEN", "no token");
                Toast toast = Toast.makeText(StickersSentActivity.this, "Cannot get token", Toast.LENGTH_SHORT);
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
                        for (DataSnapshot dschild : t.getResult().getChildren()) {
                            if (dschild.hasChildren()) {
                                // It's a Sticker Node, need to parse the data
                                int stickerID = Integer.parseInt(String.valueOf(dschild.child("stickerID").getValue()));
                                int stickerCount =0;


                                Log.d("Sticker path", String.valueOf(stickerID));

                                addHistoryItemToRecyclerView(stickerID, stickerCount);
                            }
                        }
                    }
                });
            }
        });
        generateRecyclerView();
    }

    private void addHistoryItemToRecyclerView(Integer stickerID, Integer stickerCount) {
        recyclerViewLayoutManager.smoothScrollToPosition(recyclerView, null, 0);
        StickersSent.add(0, new StickersSent(stickerID, stickerCount));
        recyclerViewAdapter.notifyItemInserted(0);
    }

    private void generateRecyclerView() {
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new StickersSentRecyclerViewAdapter(StickersSent);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

    }

}