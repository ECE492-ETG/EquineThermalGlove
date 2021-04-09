package com.example.equinethermalglove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/*
MPAndroidChart license statement:

Copyright 2020 Philipp Jahoda

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this software except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class displayNewHorse extends AppCompatActivity {

    // global variables
    private static final int maxX = 5;
    private static final float maxT = 50f;
    private static final float minT = 30f;
    private static final float maxAxis = 70f;
    private static ArrayList<String> labels = new ArrayList<>();
    private static final String SET_LABEL = "Horse Temperature Data";
    private EditText horse;
    private Spinner limb;

    BarChart barChart;
    ArrayList<Double> dt = new ArrayList<>();
    String userID;

    /**
     * Function called when activity is invoked
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_new_horse);

        // initialize variables
        final Button rtn = findViewById(R.id.return_btn);
        final Button save = findViewById(R.id.save_btn);
        horse = findViewById(R.id.horse_name);
        limb = findViewById(R.id.horseLimb);
        userID = dbManager.getAuth().getCurrentUser().getEmail();
        String[] limbOptions = {"Front Left", "Front Right", "Back Left", "Back Right"};
        ArrayAdapter<String> sAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, limbOptions);
        limb.setAdapter(sAdapt);

        // return if return button pressed
        rtn.setOnClickListener(v -> {
            Intent intent = new Intent(displayNewHorse.this, MainActivity.class);
            startActivity(intent);
        });

        // save data to database on save button pressed
        save.setOnClickListener(v -> {
            // get the chosen horse name and limb
            String h = horse.getText().toString().trim().toUpperCase();
            String l;
            if (limb.getSelectedItem() == "Front Right") {
                l = "frontRight";
            } else if(limb.getSelectedItem() == "Front Left") {
                l = "frontLeft";
            } else if (limb.getSelectedItem() == "Back Left") {
                l = "backLeft";
            } else if (limb.getSelectedItem() == "Back Right") {
                l = "backRight";
            } else {
                l = "";
            }
            // write the data to the database
            writeToDb(h, l);
            save.setVisibility(Button.GONE);
        });

        // get the data from bluetooth scan for display
        dt = (ArrayList<Double>) getIntent().getExtras().get("data");

        // create barchart and display to user
        barChart = findViewById(R.id.barchart);
        BarData data = createData();
        appearance();
        prepareData(data);
    }

    /**
     * set up the data to be displayed
     * @return
     *      the data to be displayed
     */
    private BarData createData() {
        // TODO: separate battery life from temperatures for display
        ArrayList<BarEntry> values = new ArrayList<>();
        int x;
        double y;
        // get data from bluetooth read in and set for display
        int[] colors = new int[maxX];
        for (int i = 0; i < dt.size(); i++) {
            x = i;
            // get the data from bluetooth for display
            y = dt.get(i);
            if (y > maxT) {
                colors[i] = Color.RED;
            } else if (y < minT) {
                colors[i] = Color.CYAN;
            } else {
                colors[i] = Color.GREEN;
            }
            //TODO: cast to int if not displayed correctly
            values.add(new BarEntry(x, (float) y));
        }

        BarDataSet set = new BarDataSet(values, SET_LABEL);
        set.setColors(colors);

        return new BarData(set);
    }

    /**
     * send the data to the front end for display
     * @param data
     *      the data to be displayed
     */
    private void prepareData(BarData data) {
        data.setValueTextSize(12f);
        barChart.setData(data);
        barChart.invalidate();
    }

    /**
     * set up the front end for display
     */
    private void appearance() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        XAxis x = barChart.getXAxis();
        barChart.getXAxis().setGranularityEnabled(true);
        labels.add("Thumb"); labels.add("Index"); labels.add("Middle"); labels.add("Ring"); labels.add("Pinkie");
        Object[] l = labels.toArray();
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String) l[(int) value];
            }
        });
        
        x.setLabelCount(labels.size());

        LimitLine li1 = new LimitLine(maxT);
        li1.setLineWidth(5f);
        li1.setLineColor(Color.RED);
        LimitLine li2 = new LimitLine(minT);
        li2.setLineWidth(5f);
        li2.setLineColor(Color.BLUE);

        YAxis lAxis = barChart.getAxisLeft();
        YAxis rAxis = barChart.getAxisRight();

        lAxis.setGranularity(1f);
        lAxis.setAxisMinimum(0);
        lAxis.setAxisMaximum(maxAxis);
        lAxis.addLimitLine(li1);
        lAxis.addLimitLine(li2);

        rAxis.setGranularity(1f);
        rAxis.setAxisMinimum(0);
        rAxis.setAxisMaximum(maxAxis);
        rAxis.addLimitLine(li1);
        rAxis.addLimitLine(li2);
    }

    /**
     * write the data to the firestore database
     * @param horseName
     *      the horse name
     * @param limb
     *      The limb measured
     */
    private void writeToDb(String horseName, String limb) {
        HashMap<String, Object> user = new HashMap<>();
        user.put("value", 1);
        HashMap<String, Object> data = new HashMap<>();
        data.put("temp", dt);
        Calendar curDate = Calendar.getInstance();
        String date = curDate.get(Calendar.DAY_OF_MONTH) + "-" + (curDate.get(Calendar.MONTH) + 1) + "-" +
                        curDate.get(Calendar.YEAR) + " " + curDate.get(Calendar.HOUR) + ":" +
                        curDate.get(Calendar.MINUTE) + ":" + curDate.get(Calendar.SECOND);

        // check if the horse already exists in the database
        if (!horseExists(horseName)) {
            Log.d("horse exists", "false");
            ArrayList<Double> emptyData = new ArrayList<>();
            HashMap<String, Object> empty = new HashMap<>();
            emptyData.add(0.0); emptyData.add(0.0); emptyData.add(0.0); emptyData.add(0.0); emptyData.add(0.0);
            empty.put("temp", emptyData);
            String[] limbs = {"frontLeft", "frontRight", "backLeft", "backRight"};

            dbManager.getdB().collection(userID).document(horseName).set(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("data added", "Data added to database");
                } else {
                    Log.d("data added", "data not added to database");
                }
            });

            // if horse does not exist, setup the limbs to avoid app crashing later
            for (int i = 0; i < 4; i++) {
                dbManager.getdB().collection(userID).document(horseName).collection(limbs[i]).document("init").set(empty).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("data added", "Data added to database");
                    } else {
                        Log.d("data added", "data not added to database");
                    }
                });
            }
        }

        // add the new measurement data to the database
        dbManager.getdB().collection(userID).document(horseName).collection(limb).document(date).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("data added", "success");
                } else {
                    Log.d("data added", "failure");
                }
            }
        });
    }

    /**
     * check if the horse is in the database (case sensitive)
     * @param horse
     *      The horse to be checked
     * @return
     *      true if the horse is in the database, false otherwise
     */
    private boolean horseExists(String horse) {
        final boolean[] isHorse = new boolean[1];
        dbManager.getdB().collection(userID).document(horse).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    isHorse[0] = doc.exists();
                }
            }
        });
        Log.d("isHorse", String.valueOf(isHorse[0]));
        return isHorse[0];
    }
}