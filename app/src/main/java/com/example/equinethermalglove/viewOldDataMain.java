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
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class viewOldDataMain extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> horseNames;
    ArrayAdapter<String> adapt;
    Spinner limbs;
    String userID;
    HashMap<String, Integer> data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_old_data_main);

        userID = "test";
        final Button del = findViewById(R.id.delData);
        final Button viewData = findViewById(R.id.viewData);
        final ListView horses = findViewById(R.id.horseList);
        limbs = findViewById(R.id.limbs);

        String[] limbOptions = {"Front Left", "Front Right", "Back Left", "Back Right"};
        ArrayAdapter<String> sAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, limbOptions);
        limbs.setAdapter(sAdapt);

        horseNames = new ArrayList<>();

        adapt = new ArrayAdapter<>(this, R.layout.horse_name_layout, horseNames);
        horses.setAdapter(adapt);

        del.setVisibility(Button.GONE);
        viewData.setVisibility(Button.GONE);

        // get all horse names from firestore
        db.collection(userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

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
                    String limb;
                    if (limbs.getSelectedItem() == "Front Right") {
                        limb = "frontRight";
                    } else if(limbs.getSelectedItem() == "Front Left") {
                        limb = "frontLeft";
                    } else if (limbs.getSelectedItem() == "Back Left") {
                        limb = "backLeft";
                    } else if (limbs.getSelectedItem() == "Back Right") {
                        limb = "backRight";
                    } else {
                        limb = "";
                    }
                    data.put(horseNames.get(selected), 0);
                    db.collection(userID).document(horseNames.get(selected)).collection(limb).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String temp = document.get("temp").toString();
                                    temp = temp.replaceAll(",", "");
                                    temp = temp.replaceAll("\\[", "");
                                    temp = temp.replaceAll("]", "");
                                    ArrayList<String> dt = new ArrayList<>();
                                    dt.addAll(Arrays.asList(temp.split(" ")));
                                    int avg = 0;
                                    for (int i = 0; i < dt.size(); i++) {
                                        avg += parseInt(dt.get(i));
                                    }
                                    avg = avg / dt.size();
                                    Log.d("data to be added", document.getId() + " -> " + avg);
                                    data.put(document.getId(), avg);
                                }
                            } else {
                                Log.d("horse data date", "could not read aata");
                            }
                            intent.putExtra("data", data);
                            for (Map.Entry<String, Integer> e : data.entrySet()) {
                                Log.d("data", e.getKey() + " -> " + e.getValue());
                            }
                            startActivityForResult(intent, 0);
                        }
                    });
                }
            });

            del.setOnClickListener(new View.OnClickListener() {
               final int selected = position;
                @Override
                public void onClick(View view) {
                    // TODO: delete horse data from database
                    // optional ask in fragment before delete.

                    db.collection(userID).document(horseNames.get(selected)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("delete", "Data deleted");
                            } else {
                                Log.d("delete", "data not deleted");
                            }
                        }
                    });
                    adapt.notifyDataSetChanged();
                }
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent horse) {
        super.onActivityResult(requestCode, resultCode, horse);
    }
}