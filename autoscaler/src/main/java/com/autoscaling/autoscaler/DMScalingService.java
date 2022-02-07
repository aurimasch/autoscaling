package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.AvgResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;

@Service
public class DMScalingService {

    @Autowired
    ValuesService valuesService;

    private static final double CPU_THRESHOLD = 55;
    private static final double RESPONSE_TIME_THRESHOLD = 1000;

    private double avgThroughputCurrent = 1.00;
    private double avgThroughputPrevious = 1.00;
    private double avgThroughputPastPrevious = 1.00;

    private int lastIntervalAdd = 0;
    private int last2IntervalAdd = 0;

    private int conservativeConstant = 5;

    public static final Integer MAX_REPLICAS = 8;
    public static final Integer MIN_REPLICAS = 1;

    private Timestamp lastTimeChecked;



    public Integer scale(Integer currentReplicasTotal) {

        if (!isTimeToScale())
             return currentReplicasTotal;

        double avgCPU = valuesService.getLastKnownCPU().getValue() * 100;
        Integer replicas = valuesService.getLastPodCount();
        Double throughput = valuesService.getThroughput().getValue();
        AvgResponse avgResponse = valuesService.getAVGResponse();

        avgThroughputCurrent = throughput / replicas;

        if (avgCPU > CPU_THRESHOLD) {
            if (avgResponse.getValue() > RESPONSE_TIME_THRESHOLD) {
                double anticipatedCPUAvg = 0.0;
                int replIncr = 0;
                do {
                    replIncr++;
                    anticipatedCPUAvg = ((replicas * avgCPU*((((avgThroughputCurrent/avgThroughputPrevious)*2)+(avgThroughputPrevious/avgThroughputPastPrevious))/3))/(replicas + replIncr));
                } while (anticipatedCPUAvg > CPU_THRESHOLD);

                if (replIncr != 0) {
                    lastIntervalAdd = 1;
                    last2IntervalAdd = 2;

                    int totalReplicas = replicas + replIncr;
                    System.out.println("Scaling up, current replicas " +replicas +", total: "+totalReplicas);

                    if (totalReplicas > MAX_REPLICAS)
                        totalReplicas = MAX_REPLICAS;

                    lastTimeChecked = DateUtils.now();

                    return totalReplicas;

                } else {
                    last2IntervalAdd = 0;
                }
            }
        }

        else if (avgCPU < CPU_THRESHOLD) {
            if (last2IntervalAdd == 2) {
                last2IntervalAdd = 1;
                lastIntervalAdd = 0;
            }

            else if (last2IntervalAdd == 1) {
                last2IntervalAdd = 0;
                lastIntervalAdd = 0;
            }

            else if (last2IntervalAdd == 0) {
                double coefficient_CPU = (((replicas-1) * CPU_THRESHOLD)/(replicas)) - conservativeConstant;
                int replDecr = 0;
                if (avgCPU < coefficient_CPU)
                    replDecr = 1;

                if (replDecr == 1 && replicas != 1) {
                    int totalReplicas = replicas - replDecr;
                    System.out.println("Scaling down, current replicas " +replicas +", total: "+totalReplicas);

                    if (totalReplicas < MIN_REPLICAS)
                        totalReplicas = MIN_REPLICAS;

                    lastTimeChecked = DateUtils.now();

                    return totalReplicas;
                } else {
                    lastIntervalAdd = 0;
                }

            }

        }

        else {
            lastIntervalAdd = 0;
        }

        avgThroughputPastPrevious = avgThroughputPrevious;
        avgThroughputPrevious = avgThroughputCurrent;

        lastTimeChecked = DateUtils.now();

        return currentReplicasTotal;
    }

    private boolean isTimeToScale() {
        if (lastTimeChecked == null)
            return true;

        Timestamp now = DateUtils.now();
        Timestamp then = DateUtils.fromDate(lastTimeChecked, Calendar.SECOND, 30);

        return then.before(now);
    }

}
