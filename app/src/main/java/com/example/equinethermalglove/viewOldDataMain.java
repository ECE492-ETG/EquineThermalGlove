package com.example.equinethermalglove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class viewOldDataMain extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> horseNames;
    ArrayAdapter<String> adapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_old_data_main);

        final Button del = findViewById(R.id.delData);
        final Button viewData = findViewById(R.id.viewData);
        final ListView horses = findViewById(R.id.horseList);

        horseNames = new ArrayList<>();

        adapt = new ArrayAdapter<>(this, R.layout.horse_name_layout, horseNames);
        horses.setAdapter(adapt);

        del.setVisibility(Button.GONE);
        viewData.setVisibility(Button.GONE);

        // get all horse names from firestore
        db.collection("userID").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        horseNames.add(document.getId());
                        adapt.notifyDataSetChanged();
                        Log.d("Horse name from db", document.getId());
                    }
                } else {
                    Log.d("Horse name from db", "could not get name");
                }
            }
        });

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