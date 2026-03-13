package org.wa.data.collector.service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class HealthRawData {
    private UUID externalId;
    private String source;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
    private OffsetDateTime timestamp;
    private Integer heartRate;
    private Integer steps;
    private Double sleepHours;
}
