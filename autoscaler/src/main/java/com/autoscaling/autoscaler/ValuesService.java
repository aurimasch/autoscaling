package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Service
public class ValuesService {

    private final AtomicInteger lastKnownPodNumber = new AtomicInteger();
    private final AtomicReference<Velocity> velocityLevel = new AtomicReference<>(Velocity.defaultVelocity());
    private final AtomicReference<AvgResponse> avgResponseHolder = new AtomicReference<>();
    private final AtomicReference<AvgCPU> lastKnownAvgCPU = new AtomicReference<>();
    private final AtomicReference<CPUSettings> cpuSettingsHolder = new AtomicReference<>(new CPUSettings(CPUValues.defaultUpscalingValues(), CPUValues.defaultDownscalingValues()));
    private final AtomicReference<BTCount> throughput = new AtomicReference<>();
    private final AtomicReference<SlaFactor> slaFactor = new AtomicReference<>();
    private final AtomicReference<SLAStatus> slaStatusAtomicReference = new AtomicReference<>();
    private final AtomicReference<SLA> sla = new AtomicReference<>();

    public synchronized void publishPodCount(PodCount podCount) {
        lastKnownPodNumber.set(podCount.getValue());
    }

    public synchronized Integer getLastPodCount() {
        return lastKnownPodNumber.get();
    }

    public synchronized void publishAccelerationLevel(Velocity velocityLevel) {
        this.velocityLevel.set(velocityLevel);
    }

    public synchronized Velocity getTXVelocity() {
        return velocityLevel.get();
    }

    public synchronized void publishLastKnownCPU(AvgCPU avgCpu) {
        lastKnownAvgCPU.set(avgCpu);
    }

    public synchronized AvgCPU getLastKnownCPU() {
        return lastKnownAvgCPU.get();
    }

    public synchronized void publishCPUSettings(CPUSettings cpuValues) {
        cpuSettingsHolder.set(cpuValues);
    }

    public synchronized CPUSettings getCPUSettings() {
        return cpuSettingsHolder.get();
    }

    public synchronized void publishSLAStatus(SLAStatus slaStatus) {
        slaStatusAtomicReference.set(slaStatus);
    }

    public synchronized SLAStatus getSLAStatus() {
        return slaStatusAtomicReference.get();
    }

    public synchronized void publishAvgResponse(AvgResponse avgResponse) {
        avgResponseHolder.set(avgResponse);
    }

    public synchronized AvgResponse getAVGResponse() {
        return avgResponseHolder.get();
    }

    public synchronized void publishThroughput(BTCount btCount) {
        throughput.set(btCount);
    }

    public synchronized BTCount getThroughput() {
        return throughput.get();
    }

    public synchronized void publishSlaFactor(SlaFactor slaValue) {
        slaFactor.set(slaValue);
    }

    public synchronized SlaFactor getSlaFactor() {
        return slaFactor.get();
    }
    public synchronized void publishSla(SLA lastSlaValue) {
        sla.set(lastSlaValue);
    }

    public synchronized SLA getSla() {
        return sla.get();
    }


}
