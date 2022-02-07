package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.AvgCPU;
import com.autoscaling.autoscaler.model.Velocity;
import com.autoscaling.autoscaler.model.VelocityLevel;
import com.autoscaling.autoscaler.utils.LimitedQueue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VolatilityService {

    private LimitedQueue<Velocity> velocities = new LimitedQueue<>(60);

    public void updateVelocity(Velocity velocity, AvgCPU avgCPU) {
        if (avgCPU.getValue() > 0.80)
            velocities.add(Velocity.defaultVelocity());
        else
            velocities.add(velocity);
    }

    public void updateVelocityWithZero() {
        velocities.add(Velocity.velocity(0.00, 1));
    }

    public boolean isVolatile() {

        if (velocities.isEmpty())
            return false;

        List<VelocityLevel> volatilityPattern = new ArrayList<>();

        List<Velocity> majorVelocities = velocities
                .stream()
                .filter(velocity -> velocity.getRawVelocityLevel() == VelocityLevel.MAJOR_INCREASE || velocity.getRawVelocityLevel() == VelocityLevel.MAJOR_DECREASE)
                .collect(Collectors.toList());

        for (Velocity velocity : majorVelocities) {
                addToVolatilityPattern(volatilityPattern, velocity);
        }

        return volatilityPattern.size() >= 3;

    }

    private void addToVolatilityPattern(List<VelocityLevel> volatilityPattern, Velocity velocity) {
        if (volatilityPattern.isEmpty()) {
            volatilityPattern.add(velocity.getRawVelocityLevel());
        } else {
            VelocityLevel lastElement = volatilityPattern.get(volatilityPattern.size()-1);
            if (lastElement == VelocityLevel.MAJOR_INCREASE && velocity.getRawVelocityLevel() == VelocityLevel.MAJOR_DECREASE)
                volatilityPattern.add(velocity.getRawVelocityLevel());

            if (lastElement == VelocityLevel.MAJOR_DECREASE && velocity.getRawVelocityLevel() == VelocityLevel.MAJOR_INCREASE)
                volatilityPattern.add(velocity.getRawVelocityLevel());
        }
    }

}
