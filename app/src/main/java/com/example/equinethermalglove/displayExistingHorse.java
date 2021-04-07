package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class displayExistingHorse extends AppCompatActivity {

    // global variables
    private static int maxX;
    private static final int maxY = 200;
    private static final int minY = 0;
    private static String SET_LABEL = "";
    private static ArrayList<String> labels = new ArrayList<>();

    BarChart barChart;
    HashMap<String, Double> dt;
    private Button rtn;

    /**
     * function called when the activity is invoked
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_existing_horse);

        // variable initialization
        rtn = findViewById(R.id.return_btn);
        dt = (HashMap<String, Double>) getIntent().getSerializableExtra("data");
        maxX = dt.size() - 2;
        Log.d("Check", "about to find horseName");
        String removedHorseName = "";
        String removedInit = "";
        // get the horse name chosen and remove it from the rest of the data
        for (Map.Entry<String, Double> e : dt.entrySet()) {
            Log.d("data", e.getKey() + " " + String.valueOf(e.getValue()));
            if (Objects.equals(-1.0, e.getValue())) {
                SET_LABEL = e.getKey();
                removedHorseName = e.getKey();
                Log.d("removed", "Value removed: " + e.getKey());
            }
            if (Objects.equals(0.0, e.getValue())) {
                removedInit = e.getKey();
            }
        }
        dt.remove(removedHorseName);
        dt.remove(removedInit);

        // setup the bar chart to display data
        barChart = findViewById(R.id.barchart);
        BarData data = createData();
        appearance();
        prepareData(data);

        rtn.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * creates the front end of the bar chart for display
     */
    public void appearance() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        XAxis x = barChart.getXAxis();
        Object[] l = labels.toArray();
        // set the labels for each bar in the barchart
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
     * sets the data to be displayed and displays it
     * @param data
     *      The data to be displayed
     */
    public void prepareData(BarData data) {
        data.setValueTextSize(12f);
        barChart.setData(data);
        barChart.invalidate();
    }

    /**
     * creates the data to be displayed
     * @return
     *      The new data that will be displayed to the user
     */
    public BarData createData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        int x;
        ArrayList<Double> y = new ArrayList<>();
        // sets up each bar for the barchart
        for (Map.Entry<String, Double> e : dt.entrySet()) {
            labels.add(e.getKey());
            y.add(e.getValue());
        }
        // reverses the labels and y coordinates so that they are in chronological order
        Collections.reverse(labels);
        Collections.reverse(y);
        double val;
        for (int i = 0; i < maxX; i++) {
            x = i;
            val = y.get(i);
            values.add(new BarEntry(x, (float) val));
        }

        BarDataSet set = new BarDataSet(values, SET_LABEL);

        BarData data = new BarData(set);

        return data;
    }
}