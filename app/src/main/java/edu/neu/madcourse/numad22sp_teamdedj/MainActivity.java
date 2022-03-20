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

public class MainActivity extends AppCompatActivity {

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
            Intent intent = new Intent(this, StickerMessagingActivity.class);
            // Enclose the currentUser information as a parameter
            Bundle b = new Bundle();
            b.putString("currentUser", username);
            intent.putExtras(b);
            Log.e("MAKING CURRENT USER", username);
            startActivity(intent);
        });

        alertDialogBuilder.setNegativeButton("Cancel", (dialog, whichButton) -> {
        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }

}