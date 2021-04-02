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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    // global variables;
    ArrayList<String> horseNames;
    ArrayAdapter<String> adapt;
    Spinner limbs;
    String userID;
    HashMap<String, Integer> data = new HashMap<>();
    static dbManager dbm;

    /**
     * function called when activity is invoked
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_old_data_main);

        // variable initialization
        final Button del = findViewById(R.id.delData);
        final Button viewData = findViewById(R.id.viewData);
        final ListView horses = findViewById(R.id.horseList);
        final TextView user = findViewById(R.id.username);
        limbs = findViewById(R.id.limbs);
        String[] limbOptions = {"Front Left", "Front Right", "Back Left", "Back Right"};
        ArrayAdapter<String> sAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, limbOptions);
        limbs.setAdapter(sAdapt);
        user.setText(dbManager.getAuth().getCurrentUser().getEmail());
        userID = user.getText().toString();

        // set the listview to display horses in the firestore database
        horseNames = new ArrayList<>();
        adapt = new ArrayAdapter<>(this, R.layout.horse_name_layout, horseNames);
        horses.setAdapter(adapt);

        // only show buttons if a horse is selected
        del.setVisibility(Button.GONE);
        viewData.setVisibility(Button.GONE);

        // get all horse names from firestore
        dbManager.getdB().collection(userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

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

        // when a horse is selected show buttons
        horses.setOnItemClickListener((parent, view, position, id) -> {
            del.setVisibility(Button.VISIBLE);
            viewData.setVisibility(Button.VISIBLE);

            // move to display data when view button is pressed
            viewData.setOnClickListener(new View.OnClickListener() {
                final int selected = position;

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(viewOldDataMain.this, displayExistingHorse.class);
                    String limb;
                    // get chosen limb
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
                    // get all data from chosen limb and send to next activity
                    data.put(horseNames.get(selected), -1);
                    dbManager.getdB().collection(userID).document(horseNames.get(selected)).collection(limb).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String temp = document.get("temp").toString();
                                    // remove all but the numbers from the data
                                    temp = temp.replaceAll(",", "");
                                    temp = temp.replaceAll("\\[", "");
                                    temp = temp.replaceAll("]", "");
                                    ArrayList<String> dt = new ArrayList<>();
                                    dt.addAll(Arrays.asList(temp.split(" ")));
                                    // get the average of each measurement array
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
                            startActivityForResult(intent, 0);
                        }
                    });
                }
            });

            // delete horse from database if delete is pressed
            del.setOnClickListener(new View.OnClickListener() {
               final int selected = position;
                @Override
                public void onClick(View view) {
                    // TODO: delete horse data from database
                    // optional ask in fragment before delete.

                    dbManager.getdB().collection(userID).document(horseNames.get(selected)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("delete", "Data deleted");
                                horseNames.remove(horseNames.get(selected));
                                adapt.notifyDataSetChanged();
                                del.setVisibility(Button.GONE);
                                viewData.setVisibility(Button.GONE);
                            } else {
                                Log.d("delete", "data not deleted");
                            }
                        }
                    });
                }
            });
        });
    }

    /**
     * when finish is called from another activity started by startActivityForResult()
     * @param requestCode
     *      code sent when activity called
     * @param resultCode
     *      return code
     * @param horse
     *      data returned from activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent horse) {
        super.onActivityResult(requestCode, resultCode, horse);
    }
}