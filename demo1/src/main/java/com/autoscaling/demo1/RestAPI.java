package com.autoscaling.demo1;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;


import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Random;

@RestController
public class RestAPI {


    private final Counter transactionCount;
    private final Histogram requestDuration;

    public RestAPI(MeterRegistry meterRegistry, CollectorRegistry collectorRegistry) {
        transactionCount = meterRegistry.counter("bt_count");

        requestDuration = Histogram.build()
                .name("request_duration")
                .help("Time for HTTP request.")
                .buckets(1.0, 1.5, 2.0, 100.0)
                .register(collectorRegistry);

    }

    @GetMapping("/api/execute")
    public void execute() throws InterruptedException {
        Histogram.Timer timer = requestDuration.startTimer();

        transactionCount.increment();
        var fact = factorial(8000);
        System.out.println(fact);
        try {
            Thread.sleep(getSleepTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timer.observeDuration();
    }

    private Integer getSleepTime() {
        return new Random().nextInt(700 - 200 + 1) + 200;
    }

    public static BigInteger factorial(int number) {
        BigInteger factorial = BigInteger.ONE;
        for (int i = number; i > 0; i--) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        return factorial;
    }

}
