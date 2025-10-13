package com.stkj.aoxin.weight.pay.model;

public class ReadWeightEvent {

    private double weight;


    public ReadWeightEvent(double weight) {
        this.weight = weight;
    }


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
