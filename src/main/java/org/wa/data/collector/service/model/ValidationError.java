package org.wa.data.collector.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationError {
    private String userId;
    private String reason;
    private Object rawPayload;
}
