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

        btConn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, displayNewHorse.class);
            startActivity(intent);
        });

        viewExist.setOnClickListener(v -> {
            // change back to viewOldDataMain.class when data starts being added to database
            Intent intent = new Intent(MainActivity.this, viewOldDataMain.class);
            startActivity(intent);
        });
    }
}
