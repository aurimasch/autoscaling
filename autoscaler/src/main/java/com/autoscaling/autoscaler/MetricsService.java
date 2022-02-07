package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Service
public class MetricsService {

    @Value("${prometheus.url}")
    private String prometheusURL;

    @Autowired
    private ValuesService valuesService;

    @Autowired
    private CPUAdjustor cpuAdjustor;

    @Autowired
    private VolatilityService volatilityService;

    RestTemplate restTemplate = new RestTemplate();

    private static final Long OFFSET_SECONDS = 30L;

    private static final Double TARGET_SLA = 98.00D;


    ParameterizedTypeReference<HashMap<String, Object>> responseType =
            new ParameterizedTypeReference<>() {
            };

    public void process(BTCount btCount) {
        if (btCount.getValue() == 0) {
            volatilityService.updateVelocityWithZero();
            return;
        }

        valuesService.publishThroughput(btCount);

        Double offset30SecValue = fetchValue(OFFSET_SECONDS);

        BigDecimal currentVelocity = BigDecimal.valueOf(btCount.getValue()).subtract(BigDecimal.valueOf(offset30SecValue)).divide(BigDecimal.valueOf(OFFSET_SECONDS), 2, RoundingMode.HALF_UP);

        Velocity velocity = Velocity.velocity(currentVelocity.doubleValue(), valuesService.getLastPodCount());

        System.out.println("Current velocity: " + velocity);

        volatilityService.updateVelocity(velocity, valuesService.getLastKnownCPU());
        valuesService.publishAccelerationLevel(velocity);
    }

    public void processSLA(SLA sla) {
       SLAStatus slaStatus = getSLAStatus(sla);
       CPUSettings cpuSettings = cpuAdjustor.recalculateUpScalingCPUValues(valuesService.getCPUSettings(), slaStatus);

       valuesService.publishCPUSettings(cpuSettings);
       valuesService.publishSLAStatus(slaStatus);
    }

    private SLAStatus getSLAStatus(SLA sla) {
        Double margin = BigDecimal.valueOf(100.00 - TARGET_SLA).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP).doubleValue();
        if (sla.getValue() > (TARGET_SLA + margin))
            return SLAStatus.ABOVE_TARGET;

        if (sla.getValue() < TARGET_SLA)
            return SLAStatus.BELOW_TARGET;

        return SLAStatus.ON_TARGET;
    }

    public Double fetchValue(Long offset) {
        try {
            StringBuilder builder = new StringBuilder(prometheusURL+"api/v1/query");
            builder.append("?query=");
            builder.append(URLEncoder.encode("sum by (app) (rate(bt_count_total[30s] offset "+offset+"s))", StandardCharsets.UTF_8.toString()));

            URI uri = URI.create(builder.toString());

            RequestEntity<Void> request = RequestEntity.get(uri)
                    .accept(MediaType.APPLICATION_JSON).build();


            ResponseEntity<HashMap<String, Object>> result = restTemplate.exchange(request, responseType);
            return parse(result.getBody());

        } catch (Exception e) {
            System.out.println("Reading SLA" + e.getMessage());
            return null;
        }
    }

    private Double parse(HashMap<String, Object> body) {
        JSONObject json = new JSONObject(body);

        return json.getJSONObject("data")
                .getJSONArray("result")
                .getJSONObject(0)
                .getJSONArray("value")
                .getDouble(1);
    }


}
