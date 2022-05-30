package com.autoscaling.autoscaler.model;

public class CPUValues {

    private double upper;
    private double mid;
    private double lower;

    public CPUValues(double upper, double mid, double lower) {
        this.upper = upper;
        this.mid = mid;
        this.lower = lower;
    }

    public static CPUValues defaultUpscalingValues() {
        return new CPUValues(0.75, 0.70,0.65);
    }

    public static CPUValues defaultDownscalingValues() {
        return new CPUValues(0.35, 0.30 ,0.25);
    }

    public double getUpper() {
        return upper;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    @Override
    public String toString() {
        return "CPUValues{" +
                "upper=" + upper +
                ", mid=" + mid +
                ", lower=" + lower +
                '}';
    }
}
