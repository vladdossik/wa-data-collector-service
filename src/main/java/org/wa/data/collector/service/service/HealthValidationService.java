package org.wa.data.collector.service.service;

import org.wa.data.collector.service.model.HealthRawData;
import org.wa.data.collector.service.model.ValidationResult;

public interface HealthValidationService {
    ValidationResult validateAndEnrich(HealthRawData raw);
}
