package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class displayExistingHorse extends AppCompatActivity {

    private static int maxX;
    private static final int maxY = 200;
    private static final int minY = 0;
    private static String SET_LABEL = "";
    private static ArrayList<String> labels = new ArrayList<>();

    BarChart barChart;
    HashMap<String, Integer> dt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_existing_horse);

        dt = (HashMap<String, Integer>) getIntent().getSerializableExtra("data");
        maxX = dt.size() - 1;
        for (Map.Entry<String, Integer> e : dt.entrySet()) {
            if (Objects.equals(0, e.getValue())) {
                SET_LABEL = e.getKey();
                dt.remove(e.getKey());
            }
        }
        barChart = findViewById(R.id.barchart);
        BarData data = createData();
        appearance();
        prepareData(data);
    }

    public void appearance() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        XAxis x = barChart.getXAxis();
        Object[] l = labels.toArray();
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String) l[(int) value];
            }
        });

        YAxis lAxis = barChart.getAxisLeft();
        YAxis rAxis = barChart.getAxisRight();

        lAxis.setGranularity(10f);
        lAxis.setAxisMinimum(0);

        rAxis.setGranularity(10f);
        rAxis.setAxisMinimum(0);
    }

    public void prepareData(BarData data) {
        data.setValueTextSize(12f);
        barChart.setData(data);
        barChart.invalidate();
    }

    public BarData createData() {
        ArrayList<BarEntry> values = new ArrayList<>();
        int x;
        ArrayList<Integer> y = new ArrayList<>();
        int j = 0;
        for (Map.Entry<String, Integer> e : dt.entrySet()) {
            labels.add(e.getKey());
            y.add(e.getValue());
            j++;
        }
        for (int i = 0; i < maxX; i++) {
            x = i;
            values.add(new BarEntry(x, y.get(i)));
        }

        BarDataSet set = new BarDataSet(values, SET_LABEL);;

        BarData data = new BarData(set);

        return data;
    }
}