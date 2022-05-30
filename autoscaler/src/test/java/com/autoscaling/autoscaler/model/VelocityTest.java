package com.autoscaling.autoscaler.model;

import com.autoscaling.autoscaler.ScalingService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VelocityTest {

    @Test
    public void shouldBeMajorIncrease() {
        assertEquals(VelocityLevel.MAJOR_INCREASE, new Velocity(100.0, 1).getVelocityLevel());
    }

    @Test
    public void shouldBeStable() {
        assertEquals(VelocityLevel.STABLE, new Velocity(-0.10, 1).getVelocityLevel());
    }

    @Test
    public void shouldBeFactorOf3() {
        assertEquals(3, new Velocity(110.0, 1).getIncreaseFactor());
    }

    @Test
    public void shouldBeFactorOf1() {
        assertEquals(1, new Velocity(0.1, 1).getIncreaseFactor());
    }

}