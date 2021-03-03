package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class saveNewData extends AppCompatActivity {

    Horse h;
    // TODO: add logic to display new data and save to the database
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_new_data);
    }

    public void displayHorse(Horse horse) {

    }

    // TODO: add reference to dbManager
    public void saveHorse(Horse horse /*, dbManager db*/) {

    }
}