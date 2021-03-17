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
    private static final int maxY = 70;
    private static final int minY = 1;
    private static final String SET_LABEL = "Test";
    private static final String[] labels = {"first", "second", "third", "fourth", "fifth"};

    BarChart barChart;

    // TODO: add logic for returning to database menu and deleting horse
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_new_horse);

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
            y = vals[i];
            values.add(new BarEntry(x, y));
        }

        BarDataSet set = new BarDataSet(values, SET_LABEL);;

        BarData data = new BarData(set);

        return data;
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
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return labels[(int) value];
            }
        });

        YAxis lAxis = barChart.getAxisLeft();
        YAxis rAxis = barChart.getAxisRight();

        lAxis.setGranularity(10f);
        lAxis.setAxisMinimum(0);

        rAxis.setGranularity(10f);
        rAxis.setAxisMinimum(0);
    }
}