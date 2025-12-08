package org.wa.data.collector.service.model;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class HealthRaw {
    private String userId;
    private String source;
    private OffsetDateTime timestamp;

    private Integer heartRate;
    private Integer steps;
    private Double sleepHours;
}
