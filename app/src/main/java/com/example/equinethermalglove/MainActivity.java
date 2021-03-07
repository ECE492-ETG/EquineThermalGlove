package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // TODO: add buttons for connecting to database or starting bluetooth readin
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btConn = findViewById(R.id.btConn);
        final Button viewExist = findViewById(R.id.viewExisting);
        final Button imUp = findViewById(R.id.imUpload);

        btConn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, displayHorse.class);
            startActivity(intent);
        });

        viewExist.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, viewOldDataMain.class);
            startActivity(intent);
        });

        imUp.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, imageUpload.class);
            startActivity(intent);
        });
    }
}
