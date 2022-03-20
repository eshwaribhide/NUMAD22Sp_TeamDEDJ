package edu.neu.madcourse.numad22sp_teamdedj;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void stickerMessagingActivityOnClick(View view) {

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
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast toast = Toast.makeText(this, "Cannot get token", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Log.e("CLIENT_REGISTRATION_TOKEN", task.getResult());
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("currentUser").setValue(username);
                }});

            Intent intent = new Intent(this, StickerMessagingActivity.class);
            startActivity(intent);
        });

        alertDialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

}