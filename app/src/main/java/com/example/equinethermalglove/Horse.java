package com.example.equinethermalglove;

import java.io.Serializable;
import java.util.ArrayList;

public class Horse implements Serializable {

    // TODO: add any needed logic for horse class
    protected ArrayList<ArrayList<Double>> temperatures;
    protected String name;

    public Horse(String n, ArrayList<ArrayList<Double>> temp) {
        this.temperatures = temp;
        this.name = n;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<ArrayList<Double>> getTemperatures() {
        return this.temperatures;
    }

    public void setTemperatures(ArrayList<ArrayList<Double>> t) {
        this.temperatures = t;
    }
}
