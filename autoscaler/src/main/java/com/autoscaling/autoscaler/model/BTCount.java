package com.autoscaling.autoscaler.model;

import java.sql.Timestamp;

public class BTCount {

    private Timestamp timestamp;
    private Double value;

    public BTCount(Timestamp timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "BTCount{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }

    public String toCSVLine() {
        return timestamp+","+value;

    }
}
