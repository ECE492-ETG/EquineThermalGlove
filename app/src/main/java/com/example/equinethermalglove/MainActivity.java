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

    // TODO: add buttons for connecting to database or starting bluetooth readin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btConn = findViewById(R.id.btConn);
        final Button viewExist = findViewById(R.id.viewExisting);
        final Button loginProfileButton = findViewById(R.id.login_profile_button);

        btConn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, displayNewHorse.class);
            startActivity(intent);
        });

        viewExist.setOnClickListener(v -> {
            // change back to viewOldDataMain.class when data starts being added to database
            Intent intent = new Intent(MainActivity.this, displayExistingHorse.class);
            startActivity(intent);
        });

        loginProfileButton.setOnClickListener(v -> {
            loginOrProfile();
        });

        // Redirect user to login page if no account logged in
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    loginProfileButton.setText(firebaseAuth.getCurrentUser().getEmail());
                }
                else {
                    loginProfileButton.setText(R.string.etg_login_title);
                }
            }
        });
    }

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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
