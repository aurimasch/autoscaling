package com.autoscaling.autoscaler.model;

import com.autoscaling.autoscaler.ScalingService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Velocity {

    private final Double currentVelocity;
    private final Double tolerableIncreaseVelocity;
    private final Double tolerableDecreaseVelocity;
    private int increaseFactor;
    private int decreaseFactor;

    {
        BigDecimal maxThroughput = BigDecimal.valueOf(72);
        BigDecimal totalTimeIncrease = BigDecimal.valueOf(ScalingService.MAX_REPLICAS)
                .subtract(BigDecimal.valueOf(ScalingService.MIN_REPLICAS))
                .multiply(BigDecimal.valueOf(75));

        this.tolerableIncreaseVelocity = maxThroughput.divide(totalTimeIncrease, 2, RoundingMode.HALF_UP).doubleValue();

        BigDecimal totalTimeDecrease = BigDecimal.valueOf(ScalingService.MAX_REPLICAS)
                .subtract(BigDecimal.valueOf(ScalingService.MIN_REPLICAS))
                .multiply(BigDecimal.valueOf(75));

        this.tolerableDecreaseVelocity = maxThroughput.divide(totalTimeDecrease, 2, RoundingMode.HALF_UP).doubleValue();

    }

    public Velocity(Double currentVelocity, Integer currentReplicas) {

        this.currentVelocity = currentVelocity;


        if (this.currentVelocity >=0) {
            increaseFactor = (int)Math.ceil(BigDecimal.valueOf(Math.abs(this.currentVelocity))
                                    .divide(BigDecimal.valueOf(currentReplicas), 2, RoundingMode.HALF_UP)
                                    .divide(BigDecimal.valueOf(tolerableIncreaseVelocity), 2, RoundingMode.HALF_UP).doubleValue());
        }

        if (this.currentVelocity <0) {
            decreaseFactor = (int)Math.ceil(BigDecimal.valueOf(Math.abs(this.currentVelocity))
                                    .divide( BigDecimal.valueOf(ScalingService.MAX_REPLICAS).subtract(BigDecimal.valueOf(currentReplicas)).add(BigDecimal.ONE), 2, RoundingMode.HALF_UP)
                                    .divide(BigDecimal.valueOf(tolerableDecreaseVelocity), 2, RoundingMode.HALF_UP).doubleValue());
        }
    }

    public static Velocity defaultVelocity() {
        return new Velocity(0.00, 1);
    }

    public static Velocity velocity(double averageVelocity, Integer currentReplicas) {
        return new Velocity(averageVelocity, currentReplicas);
    }

    public int getIncreaseFactor() {
        if (currentVelocity >= 0) {
            if (increaseFactor == 0)
                return 1;

            if (increaseFactor >=3)
                return 3;

            return increaseFactor;
        }

        return 1;
    }

    public int getDecreaseFactor() {
        if (currentVelocity <= 0) {
            if (decreaseFactor == 0)
                return 1;

            if (decreaseFactor >= 3)
                return 3;

            return decreaseFactor;
        }

        return 1;
    }

    public Double getCurrentVelocity() {
        return currentVelocity;
    }

    public Double getTolerableIncreaseVelocity() {
        return tolerableIncreaseVelocity;
    }

    public Double getTolerableDecreaseVelocity() {
        return tolerableDecreaseVelocity;
    }

    public VelocityLevel getVelocityLevel() {
        if (currentVelocity > 0 && increaseFactor >= 3) {
            return VelocityLevel.MAJOR_INCREASE;
        }

        if (currentVelocity > 0 && increaseFactor >=2) {
            return VelocityLevel.MODERATE_INCREASE;
        }

        if (currentVelocity < 0 && decreaseFactor >= 3) {
            return VelocityLevel.MAJOR_DECREASE;
        }

        if (currentVelocity < 0 && decreaseFactor >= 2) {
            return VelocityLevel.MODERATE_DECREASE;
        }

        return VelocityLevel.STABLE;
    }

    public VelocityLevel getRawVelocityLevel() {
        int increaseFactor = (int)Math.ceil(BigDecimal.valueOf(Math.abs(this.currentVelocity))
                .divide(BigDecimal.valueOf(tolerableIncreaseVelocity), 2, RoundingMode.HALF_UP).doubleValue());

        if (currentVelocity >=0 && increaseFactor >= 3) {
                return VelocityLevel.MAJOR_INCREASE;
        }

        if (currentVelocity <0 && increaseFactor >= 3) {
            return VelocityLevel.MAJOR_DECREASE;
        }

        return VelocityLevel.STABLE;

    }

    @Override
    public String toString() {
        return "Velocity{" +
                "averageIncreaseVelocity=" + currentVelocity +
                ", tolerableIncreaseVelocity=" + tolerableIncreaseVelocity +
                ", tolerableDecreaseVelocity=" + tolerableDecreaseVelocity +
                ", increaseFactor=" + increaseFactor +
                ", decreaseFactor=" + decreaseFactor +
                ", velocityLevel=" + getVelocityLevel() +
                '}';
    }
}
