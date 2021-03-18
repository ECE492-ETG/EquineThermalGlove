package com.example.equinethermalglove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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

        btConn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, displayNewHorse.class);
            startActivity(intent);
        });

        viewExist.setOnClickListener(v -> {
            // change back to viewOldDataMain.class when data starts being added to database
            Intent intent = new Intent(MainActivity.this, displayExistingHorse.class);
            startActivity(intent);
        });

        // Redirect user to login page if no account logged in
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    startLogin();
                }
            }
        });
    }

    public void startLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
