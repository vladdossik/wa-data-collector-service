package org.wa.data.collector.service.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class HealthValidated {
    private String userId;
    private OffsetDateTime timestamp;
    private Integer heartRate;
    private Integer steps;
    private Double sleepHours;

    private Map<String, Object> metadata;
}
