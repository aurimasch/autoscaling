package com.autoscaling.autoscaler.model;

import java.sql.Timestamp;

public class PodUpTime {

    private Timestamp timestamp;
    private Integer value;

    public PodUpTime(Timestamp timestamp, Integer value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PodUpTime{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }

    public String toCSVLine() {
        return timestamp+","+value;

    }

}
