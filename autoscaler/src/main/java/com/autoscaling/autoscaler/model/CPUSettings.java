package com.autoscaling.autoscaler.model;

public class CPUSettings {

    private CPUValues upscalingValues;
    private CPUValues downscalingValues;

    public CPUSettings(CPUValues upscalingValues, CPUValues downscalingValues) {
        this.upscalingValues = upscalingValues;
        this.downscalingValues = downscalingValues;
    }

    public CPUValues getUpscalingValues() {
        return upscalingValues;
    }

    public void setUpscalingValues(CPUValues upscalingValues) {
        this.upscalingValues = upscalingValues;
    }

    public CPUValues getDownscalingValues() {
        return downscalingValues;
    }

    public void setDownscalingValues(CPUValues downscalingValues) {
        this.downscalingValues = downscalingValues;
    }
}
