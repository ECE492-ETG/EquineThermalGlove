package com.example.equinethermalglove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    /**
     * First activity loaded when app is opened
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // variable initialization
        final Button btConn = findViewById(R.id.btConn);
        final Button viewExist = findViewById(R.id.viewExisting);
        final Button loginProfileButton = findViewById(R.id.login_profile_button);

        // only show buttons after user has registered or is logged in.
//        viewExist.setVisibility(Button.VISIBLE);
//        btConn.setVisibility(Button.VISIBLE);

        // start bluetooth connection
        btConn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, bluetoothScan.class);
            startActivity(intent);
        });

        // view old data saved for user
        viewExist.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, viewOldDataMain.class);
            startActivity(intent);
        });

        // go to login page
        loginProfileButton.setOnClickListener(v -> {
            loginOrProfile();
        });

        // Redirect user to login page if no account logged in
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    loginProfileButton.setText(firebaseAuth.getCurrentUser().getEmail());
                    viewExist.setVisibility(Button.VISIBLE);
                    btConn.setVisibility(Button.VISIBLE);
                }
                else {
                    loginProfileButton.setText(R.string.etg_login_title);
                }
            }
        });
    }

    /**
     * function called when login button is pressed
     * allows user to login or register
     */
    public void loginOrProfile() {
        if(auth.getCurrentUser() != null) {
            // go to profile
            // for now just logout
            auth.signOut();
            Toast.makeText(MainActivity.this, "You've been Logged Out", Toast.LENGTH_SHORT).show();
        }
        else { // go to login/register activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * called when activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
}
