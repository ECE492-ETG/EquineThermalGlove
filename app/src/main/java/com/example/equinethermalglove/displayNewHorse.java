package com.example.equinethermalglove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
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

public class displayNewHorse extends AppCompatActivity {

    // global variables
    private static final int maxX = 6;
    private static final int maxY = 200;
    private static final int minY = 0;
    private static final String SET_LABEL = "Horse Temperature Data";
    private static final ArrayList<String> labels = new ArrayList<>();
    private EditText horse;
    private Spinner limb;

    BarChart barChart;
    ArrayList<Integer> dt = new ArrayList<>();
    String userID;

    // TODO: display battery life

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
        dt.add(12); dt.add(34); dt.add(21); dt.add(54); dt.add(2); dt.add(88);
        limb = findViewById(R.id.horseLimb);
        userID = dbManager.getAuth().getCurrentUser().getEmail();
        String[] limbOptions = {"Front Left", "Front Right", "Back Left", "Back Right"}; labels.add("Battery Life %");
        ArrayAdapter<String> sAdapt = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, limbOptions);
        limb.setAdapter(sAdapt);

        rtn.setVisibility(Button.GONE);

        // return if return button pressed
        rtn.setOnClickListener(v -> {
            Intent intent = new Intent(displayNewHorse.this, MainActivity.class);
            startActivity(intent);
        });

        // save data to database on save button pressed
        save.setOnClickListener(v -> {
            // get the chosen horse name and limb
            String h = horse.getText().toString();
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
            rtn.setVisibility(Button.VISIBLE);
        });

        // get the data from bluetooth scan for display
        //dt = getIntent().getSerializableExtra("data");

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
        int x, y;
        // get data from bluetooth read in and set for display
        for (int i = 0; i < maxX; i++) {
            x = i;
            // get the data from bluetooth for display
            y = dt.get(i);
            values.add(new BarEntry(x, y));
        }

        BarDataSet set = new BarDataSet(values, SET_LABEL);;

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
        labels.add("Thumb"); labels.add("Index"); labels.add("Middle"); labels.add("Ring"); labels.add("Pinky");
        Object[] l = labels.toArray();
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String) l[(int) value];
            }
        });

        YAxis lAxis = barChart.getAxisLeft();
        YAxis rAxis = barChart.getAxisRight();

        lAxis.setGranularity(1f);
        lAxis.setAxisMinimum(0);

        rAxis.setGranularity(1f);
        rAxis.setAxisMinimum(0);
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
        String date = curDate.get(Calendar.DAY_OF_MONTH) + "-" + (curDate.get(Calendar.MONTH) + 1) + "-" + curDate.get(Calendar.YEAR);

        // check if the horse already exists in the database
        if (!horseExists(horseName)) {
            Log.d("horse exists", "false");
            ArrayList<Integer> emptyData = new ArrayList<>();
            HashMap<String, Object> empty = new HashMap<>();
            emptyData.add(0); emptyData.add(0); emptyData.add(0); emptyData.add(0); emptyData.add(0);
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