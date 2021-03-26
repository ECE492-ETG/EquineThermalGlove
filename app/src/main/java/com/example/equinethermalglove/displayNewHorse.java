package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class displayNewHorse extends AppCompatActivity {

    private static final int maxX = 5;
    private static final int maxY = 200;
    private static final int minY = 0;
    private static final String SET_LABEL = "Horse Temperature Data";
    private static final ArrayList<String> labels = new ArrayList<>();

    BarChart barChart;
    ArrayList<Integer> dt;

    // TODO: add logic for returning to database menu and deleting horse
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_new_horse);

        // get the data from bluetooth scan for display
        //dt = getIntent().getSerializableExtra("data");
        barChart = findViewById(R.id.barchart);
        BarData data = createData();
        appearance();
        prepareData(data);
    }

    private BarData createData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        int x, y;
        int[] vals = {12, 42, 52, 21, 67};
        for (int i = 0; i < maxX; i++) {
            x = i;
            // get the data from bluetooth for display
            //y = dt.get(i);
            y = vals[i];
            values.add(new BarEntry(x, y));
        }

        BarDataSet set = new BarDataSet(values, SET_LABEL);;

        return new BarData(set);
    }

    private void prepareData(BarData data) {
        data.setValueTextSize(12f);
        barChart.setData(data);
        barChart.invalidate();
    }

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
}