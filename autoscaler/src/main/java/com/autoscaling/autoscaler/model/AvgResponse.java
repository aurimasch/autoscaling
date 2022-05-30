package com.autoscaling.autoscaler.model;

import java.sql.Timestamp;

public class AvgResponse {

    private Timestamp timestamp;
    private Double value;

    public AvgResponse(Timestamp timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public AvgResponse setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Double getValue() {
        return value;
    }

    public AvgResponse setValue(Double value) {
        this.value = value;
        return this;
    }
    @Override
    public String toString() {
        return "BTTotal{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
    public String toCSVLine() {
        return timestamp+","+value;

    }
}
