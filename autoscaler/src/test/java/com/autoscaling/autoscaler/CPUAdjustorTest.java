package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.CPUSettings;
import com.autoscaling.autoscaler.model.CPUValues;
import com.autoscaling.autoscaler.model.SLAStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CPUAdjustorTest {

    @Test
    public void shouldReachMaxValues() {

        CPUSettings cpuSettings = new CPUSettings(CPUValues.defaultUpscalingValues(), CPUValues.defaultDownscalingValues());
        CPUAdjustor cpuAdjustor = new CustomCPUAdjustor();
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.ABOVE_TARGET);

        assertEquals(0.80, cpuSettings.getUpscalingValues().getUpper());
        assertEquals(0.70, cpuSettings.getUpscalingValues().getMid());
        assertEquals( 0.60, cpuSettings.getUpscalingValues().getLower());

        assertEquals( CPUAdjustor.DOWNSCALING_UPPER_MAX, cpuSettings.getDownscalingValues().getUpper());
        assertEquals(CPUAdjustor.DOWNSCALING_MID_MAX, cpuSettings.getDownscalingValues().getMid());
        assertEquals(CPUAdjustor.DOWNSCALING_LOWER_MAX, cpuSettings.getDownscalingValues().getLower());
    }


    @Test
    public void shouldReachMinValues() {

        CPUSettings cpuSettings = new CPUSettings(CPUValues.defaultUpscalingValues(), CPUValues.defaultDownscalingValues());
        CPUAdjustor cpuAdjustor = new CustomCPUAdjustor();
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);
        cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(cpuSettings, SLAStatus.BELOW_TARGET);

        assertEquals(0.30, cpuSettings.getUpscalingValues().getUpper());
        assertEquals(0.20, cpuSettings.getUpscalingValues().getMid());
        assertEquals( 0.10, cpuSettings.getUpscalingValues().getLower());

        assertEquals( CPUAdjustor.DOWNSCALING_UPPER_MIN, cpuSettings.getDownscalingValues().getUpper());
        assertEquals( CPUAdjustor.DOWNSCALING_MID_MIN, cpuSettings.getDownscalingValues().getMid());
        assertEquals(CPUAdjustor.DOWNSCALING_LOWER_MIN, cpuSettings.getDownscalingValues().getLower());
    }


    class CustomCPUAdjustor extends CPUAdjustor {
        @Override
        protected boolean isTimeToAdjust() {
            return true;
        }
    }


}