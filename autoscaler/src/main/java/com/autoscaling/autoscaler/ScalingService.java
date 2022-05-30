package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ScalingService {

    @Autowired
    private ValuesService valuesService;

    @Autowired
    private CoolDownService coolDownService;

    @Autowired
    private VolatilityService volatilityService;

    public static final Integer MAX_REPLICAS = 8;
    public static final Integer MIN_REPLICAS = 1;

    public Integer scale(Integer currentReplicasFromAutoscaler) {

        Integer currentReplicas = valuesService.getLastPodCount();
        Velocity txVelocity = valuesService.getTXVelocity();
        AvgCPU lastKnownAvgCPU = valuesService.getLastKnownCPU();

        if (!currentReplicas.equals(currentReplicasFromAutoscaler)) {
            System.out.println("Mismatch. Current total replicas " + currentReplicasFromAutoscaler + " known replicas "+ currentReplicas +" Lets cooldown..");
            return currentReplicasFromAutoscaler;
        }

        if (shouldUpScale(lastKnownAvgCPU, txVelocity)) {

            if (currentReplicas >= MAX_REPLICAS)
                return currentReplicasFromAutoscaler;

            if (!coolDownService.isUpScaleCoolDownPeriodOver()) {
                System.out.println("Still trying to cooldown");
                return currentReplicasFromAutoscaler;
            }

            if (txVelocity.getVelocityLevel() == VelocityLevel.MAJOR_DECREASE || txVelocity.getVelocityLevel() == VelocityLevel.MODERATE_DECREASE) {
                System.out.println("Velocity decreasing, skipping upscale");
                return currentReplicasFromAutoscaler;
            }


            System.out.println("Ok we need to upScale, average CPU: "+ lastKnownAvgCPU + " velocity: " +txVelocity + " current replicas: " + currentReplicas +" velocity level: " +txVelocity );
            System.out.println("Last known pod number was " + currentReplicas);

            Integer targetReplicas =  calculateUpscalingTargetReplicas(currentReplicas, txVelocity, lastKnownAvgCPU);

            coolDownService.setCoolDownPeriod(targetReplicas, currentReplicas);

            System.out.println("Target replicas " + targetReplicas);

            return targetReplicas;


        }

        if (shouldDownScale(lastKnownAvgCPU, txVelocity)) {
            if (currentReplicas <=1)
                return null;

            if (!coolDownService.isDownScaleCoolDownPeriodOver()) {
                System.out.println("Still trying to cooldown");
                return null;
            }

            if (volatilityService.isVolatile() && !valuesService.getSLAStatus().equals(SLAStatus.ABOVE_TARGET)) {
                System.out.println("We're below SLA target and load is volatile");
                return null;
            }

            if (txVelocity.getVelocityLevel() == VelocityLevel.MAJOR_INCREASE || txVelocity.getVelocityLevel() == VelocityLevel.MODERATE_INCREASE) {
                System.out.println("Velocity increasing, skipping downscale");
                return null;
            }

            System.out.println("Ok we need to downscale, average CPU: "+ lastKnownAvgCPU + " velocity: " +txVelocity + " replicas: " + currentReplicas +" velocity level: " +txVelocity );

            Integer targetReplicas =  calculateDownscalingTargetReplicas(currentReplicas, txVelocity, lastKnownAvgCPU);
            System.out.println("Target replicas " + targetReplicas);

            coolDownService.setCoolDownPeriod(targetReplicas, currentReplicas);

            return targetReplicas;

        }

        System.out.println("No need to scale");

        return null;
    }

    public Integer calculateUpscalingTargetReplicas(Integer currentReplicas, Velocity velocity, AvgCPU lastKnownAvgCPU) {
        Integer desiredReplicas = (int) Math.ceil(BigDecimal.valueOf(lastKnownAvgCPU.getValue()).divide(BigDecimal.valueOf(determineUpScalingCPUThreshold(velocity, lastKnownAvgCPU)), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(currentReplicas)).doubleValue());
        System.out.println("Desired replicas " + desiredReplicas);

        if (lastKnownAvgCPU.getValue() > 0.90) {
            System.out.println("Possible a peek. Ignoring Velocity");
            return desiredReplicas;
        }

        Integer targetReplicas = desiredReplicas * velocity.getIncreaseFactor();
        if (targetReplicas > MAX_REPLICAS)
            return MAX_REPLICAS;

        return targetReplicas;
    }

    public Integer calculateDownscalingTargetReplicas(Integer currentReplicas, Velocity velocity, AvgCPU lastKnownAvgCPU) {
        Integer desiredReplicas = (int) Math.floor(BigDecimal.valueOf(lastKnownAvgCPU.getValue()).divide(BigDecimal.valueOf(determineDownScalingCPUThreshold(velocity, lastKnownAvgCPU)), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(currentReplicas)).doubleValue());
        System.out.println("Desired replicas " + desiredReplicas);

        if (volatilityService.isVolatile()) {
            System.out.println("Due to volatility decreasing by 1 replica");
            return --currentReplicas;
        }

        if(lastKnownAvgCPU.getValue() > 0.50) {
            System.out.println("Possible increased througput. Ignoring Velocity");
            return desiredReplicas;
        }

        Integer targetReplicas = (int)Math.ceil(BigDecimal.valueOf(desiredReplicas).divide(BigDecimal.valueOf(velocity.getDecreaseFactor()), RoundingMode.HALF_UP).doubleValue());

        if (targetReplicas < MIN_REPLICAS)
            return MIN_REPLICAS;

        return targetReplicas;
    }

    public boolean shouldUpScale(AvgCPU avgCpu, Velocity velocity) {
        if (avgCpu == null)
            return false;

        if (avgCpu.getValue() > determineUpScalingCPUThreshold(velocity, avgCpu)) {
            return true;
        }

        return false;
    }

    public boolean shouldDownScale(AvgCPU avgCpu, Velocity velocity) {
        if (avgCpu == null)
            return false;

        if (avgCpu.getValue() < determineDownScalingCPUThreshold(velocity, avgCpu)) {
            return true;
        }

        return false;
    }

    public double determineUpScalingCPUThreshold(Velocity velocity, AvgCPU avgCPU) {

        CPUValues cpuValues = valuesService.getCPUSettings().getUpscalingValues();

        if(avgCPU.getValue() > 0.90) {
            System.out.println("Possible a peek. Ignoring Velocity");
            return cpuValues.getUpper();
        }

        if (velocity.getVelocityLevel() == VelocityLevel.STABLE)
            return cpuValues.getUpper();

        if (velocity.getVelocityLevel() == VelocityLevel.MODERATE_INCREASE)
            return cpuValues.getMid();

        if (velocity.getVelocityLevel() == VelocityLevel.MAJOR_INCREASE)
            return cpuValues.getLower();

        return cpuValues.getUpper();
    }

    public double determineDownScalingCPUThreshold(Velocity velocity, AvgCPU avgCPU) {
        CPUValues cpuValues = valuesService.getCPUSettings().getDownscalingValues();

        if (avgCPU.getValue() > 0.60) {
            System.out.println("Possible increased througput. Ignoring Velocity");
            return cpuValues.getUpper();
        }

        if (velocity.getVelocityLevel() == VelocityLevel.STABLE)
            return cpuValues.getUpper();

        if (velocity.getVelocityLevel() == VelocityLevel.MODERATE_DECREASE)
            return cpuValues.getMid();

        if (velocity.getVelocityLevel() == VelocityLevel.MAJOR_DECREASE)
            return cpuValues.getLower();

        return cpuValues.getUpper();

    }


}
