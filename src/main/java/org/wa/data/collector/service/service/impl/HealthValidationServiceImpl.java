package org.wa.data.collector.service.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wa.data.collector.service.model.HealthRaw;
import org.wa.data.collector.service.model.HealthValidated;
import org.wa.data.collector.service.model.ValidationResult;
import org.wa.data.collector.service.service.HealthValidationService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HealthValidationServiceImpl implements HealthValidationService {

    @Value("${health.validation.heart-rate.min}")
    private int minHeartRate;

    @Value("${health.validation.heart-rate.max}")
    private int maxHeartRate;

    @Value("${health.validation.steps.min}")
    private int minSteps;

    @Value("${health.validation.steps.max}")
    private int maxSteps;

    @Value("${health.validation.sleep.min}")
    private double minSleep;

    @Value("${health.validation.sleep.max}")
    private double maxSleep;

    public ValidationResult validateAndEnrich(HealthRaw raw) {
        ValidationResult userIdCheck = validateUserId(raw);
        if (!userIdCheck.isValid()) {
            return userIdCheck;
        }

        ValidationResult timestampCheck = validateTimestamp(raw);
        if (!timestampCheck.isValid()) {
            return timestampCheck;
        }

        ValidationResult dataFieldsCheck = validateDataFieldsPresent(raw);
        if (!dataFieldsCheck.isValid()) {
            return dataFieldsCheck;
        }

        ValidationResult businessRulesCheck = validateBusinessRules(raw);
        if (!businessRulesCheck.isValid()) {
            return businessRulesCheck;
        }

        String userId = raw.getUserId();
        HealthValidated val = new HealthValidated();
        val.setUserId(userId);
        val.setTimestamp(raw.getTimestamp());
        val.setHeartRate(raw.getHeartRate());
        val.setSteps(raw.getSteps());
        val.setSleepHours(raw.getSleepHours());

        Map<String, Object> meta = new HashMap<>();
        meta.put("receivedAt", OffsetDateTime.now().toString());
        meta.put("source", raw.getSource() != null ? raw.getSource() : "google");
        
        List<String> presentFields = new ArrayList<>();
        if (raw.getHeartRate() != null) presentFields.add("heartRate");
        if (raw.getSteps() != null) presentFields.add("steps");
        if (raw.getSleepHours() != null) presentFields.add("sleepHours");
        meta.put("dataFieldsPresent", presentFields);
        meta.put("isPartialData", presentFields.size() < 3);
        
        val.setMetadata(meta);

        log.debug("Validation and enrichment completed for user: {}, fields present: {}", 
                userId, presentFields);

        return ValidationResult.valid(val);
    }

    private ValidationResult validateUserId(HealthRaw raw) {
        String userId = raw.getUserId();
        if (userId == null || userId.isBlank()) {
            log.warn("Validation failed: userId is null or blank");
            return ValidationResult.invalid("user_id_missing", "userId is missing or empty", raw);
        }
        return ValidationResult.valid(null);
    }

    private ValidationResult validateTimestamp(HealthRaw raw) {
        OffsetDateTime now = OffsetDateTime.now();
        if (raw.getTimestamp() == null) {
            return ValidationResult.invalid("timestamp_missing", "timestamp is missing", raw);
        }
        if (raw.getTimestamp().isAfter(now)) {
            return ValidationResult.invalid("timestamp_in_future", "timestamp is in the future", raw);
        }
        return ValidationResult.valid(null);
    }

    private ValidationResult validateDataFieldsPresent(HealthRaw raw) {
        if (raw.getHeartRate() == null && raw.getSteps() == null && raw.getSleepHours() == null) {
            return ValidationResult.invalid("no_data_fields", 
                    "at least one data field (heartRate, steps, or sleepHours) must be present", raw);
        }
        return ValidationResult.valid(null);
    }

    private ValidationResult validateBusinessRules(HealthRaw raw) {
        if (raw.getHeartRate() != null) {
            int hr = raw.getHeartRate();
            if (hr < minHeartRate || hr > maxHeartRate) {
                return ValidationResult.invalid("heart_rate_out_of_bounds",
                        String.format("heart rate %d out of range [%d..%d]", hr, minHeartRate, maxHeartRate), raw);
            }
        }

        if (raw.getSteps() != null) {
            int steps = raw.getSteps();
            if (steps < minSteps || steps > maxSteps) {
                return ValidationResult.invalid("steps_out_of_bounds",
                        String.format("steps %d out of allowed range [%d..%d]", steps, minSteps, maxSteps), raw);
            }
        }

        if (raw.getSleepHours() != null) {
            double sh = raw.getSleepHours();
            if (sh < minSleep || sh > maxSleep) {
                return ValidationResult.invalid("sleep_out_of_bounds",
                        String.format("sleep %.2f out of allowed range [%.1f..%.1f]", sh, minSleep, maxSleep), raw);
            }
        }
        return ValidationResult.valid(null);
    }
}
