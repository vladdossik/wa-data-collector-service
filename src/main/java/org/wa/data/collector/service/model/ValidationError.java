package org.wa.data.collector.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ValidationError {
    private UUID externalId;
    private String reason;
    private Object rawPayload;
}
