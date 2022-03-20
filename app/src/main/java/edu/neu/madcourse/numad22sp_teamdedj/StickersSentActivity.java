package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class StickersSentActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private String currentUser;
    private RecyclerView recyclerView;
    private StickersSentRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<StickersSent> StickersSent = new ArrayList<>();

    public static class StickersSent {
        private final int stickerSource;
        private final String stickerCount;

        public StickersSent(int stickerSource, String stickerCount) {
            this.stickerSource = stickerSource;
            this.stickerCount = stickerCount;
        }

        public int getstickerSource() {
            return stickerSource;
        }

        public String getstickerCount() {
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
                        addHistoryItemToRecyclerView(R.drawable.hello, "Sent " + t.getResult().child("helloStickerCount").getValue() + " times");
                        addHistoryItemToRecyclerView(R.drawable.presents, "Sent " + t.getResult().child("presentStickerCount").getValue() + " times");
                        addHistoryItemToRecyclerView(R.drawable.laugh_sticker, "Sent " + t.getResult().child("laughStickerCount").getValue() + " times");
                        addHistoryItemToRecyclerView(R.drawable.burger_sticker, "Sent " + t.getResult().child("burgerStickerCount").getValue() + " times");
                    }
                });
            }
        });
        generateRecyclerView();
    }

    private void addHistoryItemToRecyclerView(Integer stickerID, String stickerCount) {
        recyclerViewLayoutManager.smoothScrollToPosition(recyclerView, null, 0);
        StickersSent.add(0, new StickersSent(stickerID, stickerCount));
        recyclerViewAdapter.notifyItemInserted(0);
    }

    private void generateRecyclerView() {
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerViewSent);
        recyclerView.setHasFixedSize(true);

        recyclerViewAdapter = new StickersSentRecyclerViewAdapter(StickersSent);

        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

    }

}