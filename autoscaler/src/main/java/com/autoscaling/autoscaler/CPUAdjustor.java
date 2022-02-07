package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.CPUSettings;
import com.autoscaling.autoscaler.model.CPUValues;
import com.autoscaling.autoscaler.model.SLAStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;

@Service
public class CPUAdjustor {

    private static final double UPSCALING_UPPER_MIN = 0.30;
    private static final double UPSCALING_MID_MIN = 0.20;
    private static final double UPSCALING_LOWER_MIN = 0.10;

    private static final double UPSCALING_UPPER_MAX = 0.80;
    private static final double UPSCALING_MID_MAX = 0.70;
    private static final double UPSCALING_LOWER_MAX = 0.60;

    public static final double DOWNSCALING_UPPER_MIN = 0.10;
    public static final double DOWNSCALING_MID_MIN = 0.20;
    public static final double DOWNSCALING_LOWER_MIN = 0.30;

    public static final double DOWNSCALING_UPPER_MAX = 0.40;
    public static final double DOWNSCALING_MID_MAX = 0.50;
    public static final double DOWNSCALING_LOWER_MAX = 0.60;

    private static final Integer ADJUSTMENT_INTERVAL_IN_SECONDS = 3 * 60;

    private Timestamp lastAdjustmentTime = DateUtils.now();

    public CPUSettings recalculateUpScalingCPUValues(CPUSettings cpuSettings, SLAStatus slaStatus) {

        if (!isTimeToAdjust())
            return cpuSettings;

        lastAdjustmentTime = DateUtils.now();

        if (slaStatus == SLAStatus.ON_TARGET)
            return cpuSettings;

        if (slaStatus == SLAStatus.ABOVE_TARGET) {
            CPUSettings newIncreasedCPUValues = adjustCPUValues(cpuSettings, 0.05, 0.03);
            System.out.println("Old upscaling CPU values "+cpuSettings.getUpscalingValues()+" new CPU values "+newIncreasedCPUValues.getUpscalingValues()+". Old downscaling CPU values "+cpuSettings.getDownscalingValues()+" new CPU values "+newIncreasedCPUValues.getDownscalingValues());
            return newIncreasedCPUValues;
        }

        if (slaStatus == SLAStatus.BELOW_TARGET) {
            CPUSettings newDecreasedCPUValues = adjustCPUValues(cpuSettings, -0.10, -0.06);
            System.out.println("Old upscaling CPU values "+cpuSettings.getUpscalingValues()+" new CPU values "+newDecreasedCPUValues.getUpscalingValues()+". Old downscaling CPU values "+cpuSettings.getDownscalingValues()+" new CPU values "+newDecreasedCPUValues.getDownscalingValues());
            return newDecreasedCPUValues;
        }

        return cpuSettings;

    }

    private CPUSettings adjustCPUValues(CPUSettings cpuSettings, double upscalingAdjustment, double downscalingAdjustment) {

        CPUValues upscalingValues = cpuSettings.getUpscalingValues();
        CPUValues downScalingValues = cpuSettings.getDownscalingValues();

        upscalingValues =  calculateUpscalingValues(upscalingAdjustment, upscalingValues);

        downScalingValues =  calculateDownscalingValues(downscalingAdjustment, downScalingValues);

        return new CPUSettings(upscalingValues, downScalingValues);

    }

    private CPUValues calculateUpscalingValues(double adjustment, CPUValues upscalingValues) {
        double lowerValue = BigDecimal.valueOf(upscalingValues.getLower()).add(BigDecimal.valueOf(adjustment)).doubleValue();
        double midValue = BigDecimal.valueOf(upscalingValues.getMid()).add(BigDecimal.valueOf(adjustment)).doubleValue();
        double upperValue = BigDecimal.valueOf(upscalingValues.getUpper()).add(BigDecimal.valueOf(adjustment)).doubleValue();

        if (lowerValue > UPSCALING_LOWER_MAX)
            lowerValue = UPSCALING_LOWER_MAX;

        if (midValue > UPSCALING_MID_MAX)
            midValue = UPSCALING_MID_MAX;

        if (upperValue > UPSCALING_UPPER_MAX)
            upperValue = UPSCALING_UPPER_MAX;

        if (lowerValue < UPSCALING_LOWER_MIN)
            lowerValue = UPSCALING_LOWER_MIN;

        if (midValue < UPSCALING_MID_MIN)
            midValue = UPSCALING_MID_MIN;

        if (upperValue < UPSCALING_UPPER_MIN)
            upperValue = UPSCALING_UPPER_MIN;




        return new CPUValues(upperValue, midValue, lowerValue);
    }

    private CPUValues calculateDownscalingValues(double adjustment, CPUValues downscalingValues) {
        double lowValue = BigDecimal.valueOf(downscalingValues.getLower()).add(BigDecimal.valueOf(adjustment)).doubleValue();
        double midValue = BigDecimal.valueOf(downscalingValues.getMid()).add(BigDecimal.valueOf(adjustment)).doubleValue();
        double topValue = BigDecimal.valueOf(downscalingValues.getUpper()).add(BigDecimal.valueOf(adjustment)).doubleValue();

        if (lowValue > DOWNSCALING_LOWER_MAX)
            lowValue = DOWNSCALING_LOWER_MAX;

        if (midValue > DOWNSCALING_MID_MAX)
            midValue = DOWNSCALING_MID_MAX;

        if (topValue > DOWNSCALING_UPPER_MAX)
            topValue = DOWNSCALING_UPPER_MAX;

        if (lowValue < DOWNSCALING_LOWER_MIN)
            lowValue = DOWNSCALING_LOWER_MIN;

        if (midValue < DOWNSCALING_MID_MIN)
            midValue = DOWNSCALING_MID_MIN;

        if (topValue < DOWNSCALING_UPPER_MIN)
            topValue = DOWNSCALING_UPPER_MIN;


        return new CPUValues(topValue, midValue, lowValue);
    }

    protected boolean isTimeToAdjust() {
        if (lastAdjustmentTime == null)
            return true;

        Timestamp now = DateUtils.now();
        Timestamp then = DateUtils.fromDate(lastAdjustmentTime, Calendar.SECOND, ADJUSTMENT_INTERVAL_IN_SECONDS);

        return then.before(now);
    }

}
