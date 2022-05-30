package com.autoscaling.autoscaler.model;


public class SlaFactor {

    private Double value;

    public SlaFactor(Double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SLAFactor{" +
                "value=" + value +
                '}';
    }

}
