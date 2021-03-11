package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Random;

public class displayExistingHorse extends AppCompatActivity {

    private static final String SET_LABEL = "Test";
    LineChart lineChart;
    private int maxX, maxY;
    private int minX = 0, minY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_existing_horse);

        lineChart = findViewById(R.id.linechart);
        LineData data = createData();
        appearance();
        prepareData(data);
    }

    public void appearance() {
        lineChart.getDescription().setEnabled(false);
        XAxis x = lineChart.getXAxis();

        YAxis lAxis = lineChart.getAxisLeft();
        YAxis rAxis = lineChart.getAxisRight();

        lAxis.setGranularity(10f);
        lAxis.setAxisMinimum(0);

        rAxis.setGranularity(10f);
        rAxis.setAxisMinimum(0);
    }

    public void prepareData(LineData data) {
        data.setValueTextSize(12f);
        lineChart.setData(data);
        lineChart.invalidate();
    }

    public LineData createData() {
        ArrayList<Entry> values = new ArrayList<>();
        int x, y;
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            x = rand.nextInt(100);
            y = rand.nextInt(100);
            values.add(new Entry(x, y));
        }

        LineDataSet set = new LineDataSet(values, SET_LABEL);;

        LineData data = new LineData(set);
        return data;
    }
}