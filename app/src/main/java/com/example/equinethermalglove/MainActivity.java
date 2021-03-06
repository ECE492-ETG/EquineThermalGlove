package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

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

        });

        viewExist.setOnClickListener(v -> {

        });

        imUp.setOnClickListener(v -> {

        });
    }
}
