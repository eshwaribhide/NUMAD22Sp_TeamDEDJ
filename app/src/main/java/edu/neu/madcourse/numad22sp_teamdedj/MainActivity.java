package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Details are in 'fcm' package
    public void stickerMessagingActivityOnClick(View view) {
        Intent intent = new Intent(this, StickerMessagingActivity.class);
        startActivity(intent);
    }

}