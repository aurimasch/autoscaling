package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.autoscaling.autoscaler.model.SLA;

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

    public static final Integer MAX_REPLICAS = 22;
    public static final Integer MIN_REPLICAS = 1;

    private double SLANow = 101.00;
    private double SLABefore = 101.00;
    private double SLADiff = 0;

    public Integer scale(Integer currentReplicasFromAutoscaler) {

        
        Integer currentReplicas = valuesService.getLastPodCount();
        Velocity txVelocity = valuesService.getTXVelocity();
        AvgCPU lastKnownAvgCPU = valuesService.getLastKnownCPU();
        SlaFactor slaFactor = valuesService.getSlaFactor();
        SLA sla = valuesService.getSla();

        SLABefore = SLANow;
        SLANow = sla.getValue();
        SLADiff = SLANow-SLABefore;
        System.out.println("SAAA is running");

        if (!currentReplicas.equals(currentReplicasFromAutoscaler)) {
            System.out.println("Mismatch. CPA provided replicas count (run now) " + currentReplicasFromAutoscaler + "  replicas from Prometheus "+ currentReplicas +" Lets cooldown..");
            return currentReplicasFromAutoscaler;
        }

        if (shouldUpScale(lastKnownAvgCPU, txVelocity, slaFactor)) {
            System.out.println("SLA now:" + SLANow + "SLA before:" + SLABefore + "the diff: " + SLADiff);
            if (currentReplicas >= MAX_REPLICAS)
                return currentReplicasFromAutoscaler;

            if (!coolDownService.isUpScaleCoolDownPeriodOver()) {
                if(!valuesService.getSLAStatus().equals(SLAStatus.BELOW_TARGET)) {
                    if ( SLADiff >= 0.0) {
                    System.out.println("Still trying to upscale cooldown");
                    return currentReplicasFromAutoscaler;
                    }
                }
            }

            if (txVelocity.getVelocityLevel() == VelocityLevel.MAJOR_DECREASE || txVelocity.getVelocityLevel() == VelocityLevel.MODERATE_DECREASE) {
                System.out.println("Velocity decreasing, skipping upscale");
                return currentReplicasFromAutoscaler;
            }

            System.out.println("Ok we need to upScale, average CPU: "+ lastKnownAvgCPU + " velocity: " +txVelocity + " current replicas: " + currentReplicas +" velocity level: " +txVelocity  + "slafacror: " + slaFactor);

            Integer targetReplicas =  calculateUpscalingTargetReplicas(currentReplicas, txVelocity, lastKnownAvgCPU, slaFactor);

            coolDownService.setCoolDownPeriod(targetReplicas, currentReplicas,txVelocity);

            System.out.println("Upscale target replicas " + targetReplicas);

            return targetReplicas;

        }

        if (shouldDownScale(lastKnownAvgCPU, txVelocity, slaFactor)) {
            System.out.println("SLA now:" + SLANow + "SLA before:" + SLABefore + "the diff: " + SLADiff);
            if (currentReplicas <=1)
                return null;

            if (!coolDownService.isDownScaleCoolDownPeriodOver()) {

                System.out.println("Still trying to cooldown downscale action");
                return null;
            }

            if (txVelocity.getVelocityLevel() == VelocityLevel.MAJOR_INCREASE || txVelocity.getVelocityLevel() == VelocityLevel.MODERATE_INCREASE)  {
                System.out.println("Velocity increasing, skipping downscale");
                return null;
            }

            System.out.println("Ok we need to downscale, average CPU: "+ lastKnownAvgCPU + " velocity: " +txVelocity + " replicas: " + currentReplicas +" velocity level: " +txVelocity  + "and SLA factor is " +slaFactor);

            Integer targetReplicas =  calculateDownscalingTargetReplicas(currentReplicas, txVelocity, lastKnownAvgCPU, slaFactor);
            System.out.println("Downscale target replicas " + targetReplicas);

            coolDownService.setCoolDownPeriod(targetReplicas, currentReplicas, txVelocity);

            return targetReplicas;

        }

        System.out.println("No need to scale!");

        return null;
    }

    public Integer calculateUpscalingTargetReplicas(Integer currentReplicas, Velocity velocity, AvgCPU lastKnownAvgCPU, SlaFactor slaFactor) {
   
        Integer desiredReplicas = (int) Math.ceil(BigDecimal.valueOf(lastKnownAvgCPU.getValue()).divide(BigDecimal.valueOf(determineUpScalingCPUThreshold(velocity, lastKnownAvgCPU)), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(currentReplicas)).doubleValue()+1);
        System.out.println("Desired upscale replicas " + desiredReplicas);
        

        if (lastKnownAvgCPU.getValue() > CPUValues.defaultUpscalingValues().getUpper()) {
            System.out.println("Possible a peek. Ignoring Velocity");
            if ( !valuesService.getSLAStatus().equals(SLAStatus.BELOW_TARGET)) {
            if(SLADiff >= 0){
                System.out.println("We are ok with SLA.  Desired upscale replicas nr during peak is " + desiredReplicas);
                return desiredReplicas  > MAX_REPLICAS ? MAX_REPLICAS : desiredReplicas;
            } 
            }
            
        }

        Integer targetReplicas = desiredReplicas * velocity.getIncreaseFactor();

        System.out.println("Original Increase factor is: " + velocity.getIncreaseFactor() + " And original replicas : " + targetReplicas + "Desired upscale replicas " + desiredReplicas);

        if ( valuesService.getSLAStatus().equals(SLAStatus.BELOW_TARGET)) {

            //Calculating target replicas taking into account Propotional (SLOFactor = SLOTarget/SLONow) and derivative (SLOTarget-SLOnow)*Rtarget weights
            Integer diffTarget = (int) Math.ceil(BigDecimal.valueOf(98.00) 
            .subtract((BigDecimal.valueOf(slaFactor.getValue()).multiply(BigDecimal.valueOf(98.00))))
            .multiply(BigDecimal.valueOf(desiredReplicas)).divide(BigDecimal.valueOf(100.0)).doubleValue()); //derivative increase
            
            System.out.println("We are bellow SLA. Using SLA factor for  upscale replicas calculation. Original target Replicas nr is " + targetReplicas + "  and slo derivative cofficient increase" + diffTarget + "Propotional cofficient :" + slaFactor.getValue()  );
            // Rtarget = Roundup(rTargetOriginal*(1 + SLAnow/SLATarget+(SLAtarget-SLAnow)))
            Integer slaImpactedTarget = ((int) Math.ceil(BigDecimal.valueOf(desiredReplicas).divide(BigDecimal.valueOf(slaFactor.getValue()),4, RoundingMode.HALF_UP).doubleValue())) + diffTarget;

            if (slaFactor.getValue() < 0.95)
            {
                targetReplicas = slaImpactedTarget;
                System.out.println("Final Upscale with SLA impact " + targetReplicas);
            }
            else {    
                targetReplicas = slaImpactedTarget * velocity.getIncreaseFactor();
                System.out.println("Final Upscale with SLA and Velocity impact " + targetReplicas);
            }
        }

        if (targetReplicas > MAX_REPLICAS) {
            System.out.println("Returning MAX");
            return MAX_REPLICAS;
        }

        System.out.println("Final Target replicas :" + targetReplicas);

        return targetReplicas;
    }

    public Integer calculateDownscalingTargetReplicas(Integer currentReplicas, Velocity velocity, AvgCPU lastKnownAvgCPU, SlaFactor slaFactor) {
        
        double downScaleCPU = determineDownScalingCPUThreshold(velocity, lastKnownAvgCPU);
        
        System.out.println("LastKnownAvgCPUn" + lastKnownAvgCPU.getValue() + "by Treshold: " + downScaleCPU + " and decrease factor " + velocity.getDecreaseFactor());

        Integer desiredReplicas = (int) Math.ceil(BigDecimal.valueOf(lastKnownAvgCPU.getValue()).divide(BigDecimal.valueOf(determineDownScalingCPUThreshold(velocity, lastKnownAvgCPU)), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(currentReplicas)).doubleValue());
        
        System.out.println("Desired downscaling replicas " + desiredReplicas);

        if(lastKnownAvgCPU.getValue() > CPUValues.defaultUpscalingValues().getLower()) {
            System.out.println("CPU shows Possible increased througput. Ignoring Velocity");
            return currentReplicas;
        }

        Integer targetReplicas = desiredReplicas;
        
        if (volatilityService.isVolatile()) {
            System.out.println("Downscale and below SLA:Due to volatility adjusting target relicas ammont to ensure no resource drop more then Impact factor x 10%");
            return currentReplicas == MIN_REPLICAS ? MIN_REPLICAS : (int) Math.floor(currentReplicas * (1 - 0.1 * velocity.getDecreaseFactor())) < 3 ? currentReplicas : (int) Math.floor(currentReplicas * (1 - 0.1 * velocity.getDecreaseFactor()));
        }

        if (targetReplicas < MIN_REPLICAS)
            return MIN_REPLICAS;
        System.out.println("We are on SLA target and traffic is not volatile. Unvalidated target replicas are used for downscale (decrease factor is applied)");
        return targetReplicas;
    }

    public boolean shouldUpScale(AvgCPU avgCpu, Velocity velocity, SlaFactor slaFactor) {
        CPUValues cpuValuesU = valuesService.getCPUSettings().getUpscalingValues();
        
        if (avgCpu == null)
            return false;
        
        if (valuesService.getSLAStatus().equals(SLAStatus.BELOW_TARGET) ) 
        {
            if ( SLADiff < 0.0) {
                if(avgCpu.getValue() > cpuValuesU.getUpper()/2.0)
                {
                    System.out.println("shouldUpscale: Bellow SLA and SLA is decreasing.");
                    return true;
                }
            }
        }
        
        if (avgCpu.getValue() > determineUpScalingCPUThreshold(velocity, avgCpu)) {
            System.out.println("shouldUpscale: Upscaling in nomal conditions");
            return true;
        }

        return false;
    }

    public boolean shouldDownScale(AvgCPU avgCpu, Velocity velocity, SlaFactor slaFactor) {
        
        if (avgCpu == null)
            return false;
        
        if (SLADiff < 0 )
            {
                System.out.println("SLA IS DROPPING. SKIPPING DOWNSCALE");
                return false;
        }
        
        if (slaFactor.getValue() < 1.00125 ) {
        
        if( Math.abs(velocity.getCurrentVelocity())==0.0 && avgCpu.getValue() < 0.025 )
        { 
            System.out.println("shouldDownScale: Downsacale due to no load  during LOW SLA");
            return true;
        } 
        
        System.out.println("shouldDownScale: No downsacale action due to to LOW SLA");
        return false;
        
    }

        if (avgCpu.getValue() < determineDownScalingCPUThreshold(velocity, avgCpu)) {
            System.out.println("shouldDownScale: downscale in normal conditions");
            return true;
        }

        return false;
    }

    public double determineUpScalingCPUThreshold(Velocity velocity, AvgCPU avgCPU) {

        CPUValues cpuValues = valuesService.getCPUSettings().getUpscalingValues();
       
        if(avgCPU.getValue() > cpuValues.getUpper()) {
            System.out.println("Possible a peek. Ignoring Velocity");
            return cpuValues.getUpper();
        }

        if(SLADiff < 0.0)
        {
            System.out.println("SLA decresing, need to upscale sooner");
            return cpuValues.getLower();
        }

        if (velocity.getVelocityLevel() == VelocityLevel.STABLE)
            {System.out.println("This CPU upscale threshhold values selected due to  STABLE mode" + cpuValues.getUpper());
            return cpuValues.getUpper();
        }
       
        if (velocity.getVelocityLevel() == VelocityLevel.MODERATE_INCREASE){
            System.out.println("This CPU upscale threshhold values selected due to  MODERATE mode" + cpuValues.getMid());
            return cpuValues.getMid();
        }

        if (velocity.getVelocityLevel() == VelocityLevel.MAJOR_INCREASE){
            System.out.println(" This CPU upscale threshhold values selected due to  MAJOR mode" + cpuValues.getLower());
            return cpuValues.getLower();    
        }
        return cpuValues.getUpper();
    }

    public double determineDownScalingCPUThreshold(Velocity velocity, AvgCPU avgCPU) {
        CPUValues cpuValues = valuesService.getCPUSettings().getDownscalingValues();
              
        if (avgCPU.getValue() > CPUValues.defaultUpscalingValues().getLower()) {
            System.out.println("Possible increased througput. Ignoring Velocity");
            return cpuValues.getUpper();
        }
        if (velocity.getCurrentVelocity() <= 0) {

        if (velocity.getVelocityLevel() == VelocityLevel.STABLE){
            System.out.println("This downscale CPU threshhold values selected due to STABLE mode " + cpuValues.getUpper());
            return cpuValues.getUpper();}

        if (velocity.getVelocityLevel() == VelocityLevel.MODERATE_DECREASE){
            System.out.println("This downscale CPU threshhold values selected due to Moderate Decrease  " + cpuValues.getMid());
            return cpuValues.getMid();}

        if (velocity.getVelocityLevel() == VelocityLevel.MAJOR_DECREASE){
            System.out.println("This downscale CPU threshhold values selected due to major mode" + cpuValues.getLower());
            return cpuValues.getLower();}
        }

        return cpuValues.getUpper();


    }

}
