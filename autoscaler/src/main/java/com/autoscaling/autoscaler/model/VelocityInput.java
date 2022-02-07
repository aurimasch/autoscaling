package com.autoscaling.autoscaler.model;

public class VelocityInput {

    private Integer maxReplicas;
    private Integer minReplicas;
    private Integer maxThroughput;
    private Integer increaseTimeConstant;
    private Integer decreaseTimeConstant;
    private Integer currentReplicas;

    public Integer getMaxReplicas() {
        return maxReplicas;
    }

    public VelocityInput setMaxReplicas(Integer maxReplicas) {
        this.maxReplicas = maxReplicas;
        return this;
    }

    public Integer getMinReplicas() {
        return minReplicas;
    }

    public VelocityInput setMinReplicas(Integer minReplicas) {
        this.minReplicas = minReplicas;
        return this;
    }

    public Integer getMaxThroughput() {
        return maxThroughput;
    }

    public VelocityInput setMaxThroughput(Integer maxThroughput) {
        this.maxThroughput = maxThroughput;
        return this;
    }

    public Integer getIncreaseTimeConstant() {
        return increaseTimeConstant;
    }

    public VelocityInput setIncreaseTimeConstant(Integer increaseTimeConstant) {
        this.increaseTimeConstant = increaseTimeConstant;
        return this;
    }

    public Integer getCurrentReplicas() {
        return currentReplicas;
    }

    public VelocityInput setCurrentReplicas(Integer currentReplicas) {
        this.currentReplicas = currentReplicas;
        return this;
    }

    public Integer getDecreaseTimeConstant() {
        return decreaseTimeConstant;
    }

    public VelocityInput setDecreaseTimeConstant(Integer decreaseTimeConstant) {
        this.decreaseTimeConstant = decreaseTimeConstant;
        return this;
    }
}
