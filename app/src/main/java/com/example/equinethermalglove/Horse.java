package com.example.equinethermalglove;

import java.util.ArrayList;

public class Horse {

    // TODO: add any needed logic for horse class
    protected ArrayList<Double> measurements;

    public Horse(ArrayList<Double> h) {
        this.measurements = h;
    }

    public ArrayList<Double> getMeasurements() {
        return this.measurements;
    }

    public void setMeaurements(ArrayList<Double> h) {
        this.measurements = h;
    }
}
