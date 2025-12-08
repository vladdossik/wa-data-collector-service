package org.wa.data.collector.service.service;

import org.wa.data.collector.service.model.HealthRaw;
import org.wa.data.collector.service.model.ValidationResult;

public interface HealthValidationService {
    ValidationResult validateAndEnrich(HealthRaw raw);
}
