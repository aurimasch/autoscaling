package com.autoscaling.autoscaler;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AutoscalerREST {

    @Autowired
    ScalingService scalingService;

    @Autowired
    DMScalingService dmScalingService;

    @GetMapping("/api/count")
    public Integer getScaleCount(@RequestParam Map<String,String> allParams) {
       JSONObject json = new JSONObject(allParams.get("value"));
       Integer currentReplicas = json.getJSONObject("resource").getJSONObject("spec").getInt("replicas");

       return scalingService.scale(currentReplicas);
    }
}
