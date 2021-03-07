package com.example.equinethermalglove;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class displayHorse extends AppCompatActivity {

    ArrayList<Integer> testData = new ArrayList<>();
    // TODO: add logic for returning to database menu and deleting horse
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        testData.add(12); testData.add(62); testData.add(20); testData.add(42); testData.add(15);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_horse);

        BarChart barChart = (BarChart) findViewById(R.id.barchart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(testData.get(0), 0));
        entries.add(new BarEntry(testData.get(1), 1));
        entries.add(new BarEntry(testData.get(2), 2));
        entries.add(new BarEntry(testData.get(3), 3));
        entries.add(new BarEntry(testData.get(4), 4));

        BarDataSet bardataset = new BarDataSet(entries, "Cells");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("input1");
        labels.add("input2");
        labels.add("input3");
        labels.add("input4");
        labels.add("input5");

        BarData data = new BarData((IBarDataSet) labels, bardataset);
        barChart.setData(data); // set the data and list of labels into chart
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.animateY(5000);
    }

    public void returnToView() {

    }

    public void deleteHorse() {

    }
}