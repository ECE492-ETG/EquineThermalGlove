package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class viewOldDataMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_old_data_main);

        final Button del = findViewById(R.id.delData);
        final Button viewData = findViewById(R.id.viewData);
        final ListView horses = findViewById(R.id.horseList);

        del.setVisibility(Button.GONE);
        viewData.setVisibility(Button.GONE);

        horses.setOnItemClickListener((parent, view, position, id) -> {
            del.setVisibility(Button.VISIBLE);
            viewData.setVisibility(Button.VISIBLE);

            viewData.setOnClickListener(new View.OnClickListener() {
                final int selected = position;

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(viewOldDataMain.this, displayExistingHorse.class);
                    // TODO: get data for selected horse and send to new activity
                    startActivity(intent);
                }
            });

            del.setOnClickListener(new View.OnClickListener() {
               final int selected = position;
                @Override
                public void onClick(View view) {
                    // TODO: delete horse data from database
                    // optional ask in fragment before delete.
                }
            });
        });


    }
}