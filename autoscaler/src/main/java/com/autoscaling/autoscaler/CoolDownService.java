package com.autoscaling.autoscaler;

import org.springframework.stereotype.Service;

import com.autoscaling.autoscaler.model.Velocity;


import java.sql.Timestamp;
import java.util.Calendar;


@Service
public class CoolDownService {
    

    private Timestamp lastTimeUpScaled;
    private Timestamp lastTimeDownScaled;
    private static Integer UPSCALE_COOLDOWN_PERIOD_IN_SECONDS = 20;
    private Integer DOWNSCALE_COOLDOWN_PERIOD_IN_SECONDS = 20;

    public boolean isUpScaleCoolDownPeriodOver() {
        if (lastTimeUpScaled == null)
            return true;

        Timestamp now = DateUtils.now();
        Timestamp then = DateUtils.fromDate(lastTimeUpScaled, Calendar.SECOND, UPSCALE_COOLDOWN_PERIOD_IN_SECONDS);

        return then.before(now);
    }

    public boolean isDownScaleCoolDownPeriodOver() {
        if (lastTimeDownScaled == null)
            return true;

        Timestamp now = DateUtils.now();
        Timestamp then = DateUtils.fromDate(lastTimeDownScaled, Calendar.SECOND, DOWNSCALE_COOLDOWN_PERIOD_IN_SECONDS);

        return then.before(now);
    }

    public void setCoolDownPeriod(Integer targetReplicas, Integer currentReplicas, Velocity txVelocity) {
        if (upScale(targetReplicas, currentReplicas)) {
            lastTimeUpScaled = DateUtils.now();
            setUpScaleCooldown((40-10 * txVelocity.getIncreaseFactor()));
            setDownScaleCooldown(40 );
            return;
            }
        
        if (downScale(targetReplicas, currentReplicas)) {
            lastTimeDownScaled = DateUtils.now();
                setUpScaleCooldown(10);
                setDownScaleCooldown(10 * txVelocity.getDecreaseFactor());
                return;
            }

        }

    private boolean upScale(Integer targetReplicas, Integer currentReplicas) {
        return targetReplicas > currentReplicas;
    }

    private boolean downScale(Integer targetReplicas, Integer currentReplicas) {
        return targetReplicas <currentReplicas;
    }

    private void setUpScaleCooldown(Integer upsScaleCoolDownPeriod) {
        System.out.println("Setting cooldown period upscale: " + upsScaleCoolDownPeriod);
        this.UPSCALE_COOLDOWN_PERIOD_IN_SECONDS = upsScaleCoolDownPeriod;
    }

    private void setDownScaleCooldown(Integer downScaleCoolDownPeriodSeconds) {
        System.out.println("Setting cooldown downscale: " + downScaleCoolDownPeriodSeconds);
        this.DOWNSCALE_COOLDOWN_PERIOD_IN_SECONDS = downScaleCoolDownPeriodSeconds;
    }

}
