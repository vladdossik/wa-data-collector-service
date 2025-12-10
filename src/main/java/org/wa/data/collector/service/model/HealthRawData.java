package org.wa.data.collector.service.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class HealthRawData {
    private String userId;
    private String source;
    
    private OffsetDateTime timestamp;
    private String rawTimestamp;
    
    @JsonSetter("timestamp")
    public void setTimestampFromString(String timestampString) {
        this.rawTimestamp = timestampString;
        if (timestampString != null && !timestampString.isEmpty()) {
            try {
                this.timestamp = OffsetDateTime.parse(timestampString, 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
            } catch (Exception e) {
                this.timestamp = null;
            }
        } else {
            this.timestamp = null;
        }
    }
    
    private Integer heartRate;
    private Integer steps;
    private Double sleepHours;
}
