package org.wa.data.collector.service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.wa.data.collector.service.mapper.HealthMapper;
import org.wa.data.collector.service.model.HealthRawData;
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
@RequiredArgsConstructor
public class HealthValidationServiceImpl implements HealthValidationService {

    private final HealthMapper healthMapper;

    @Value("${health.constraints.heart-rate.min}")
    private int minHeartRate;

    @Value("${health.constraints.heart-rate.max}")
    private int maxHeartRate;

    @Value("${health.constraints.steps.min}")
    private int minSteps;

    @Value("${health.constraints.steps.max}")
    private int maxSteps;

    @Value("${health.constraints.sleep.min}")
    private double minSleep;

    @Value("${health.constraints.sleep.max}")
    private double maxSleep;

    public ValidationResult validateAndEnrich(HealthRawData rawData) {
        ValidationResult validationResult = validateAll(rawData);
        if (!validationResult.isValid()) {
            return validationResult;
        }

        HealthValidated validated = healthMapper.toHealthValidated(rawData);
        Map<String, Object> metadata = buildMetadata(rawData);
        validated.setMetadata(metadata);

        log.debug("Validation and enrichment completed for user: {}, fields present: {}",
                rawData.getUserId(), metadata.get("dataFieldsPresent"));

        return ValidationResult.valid(validated);
    }

    private ValidationResult validateAll(HealthRawData rawData) {
        ValidationResult userIdCheck = validateUserId(rawData);
        if (!userIdCheck.isValid()) {
            return userIdCheck;
        }

        ValidationResult dataFieldsCheck = validateDataFieldsPresent(rawData);
        if (!dataFieldsCheck.isValid()) {
            return dataFieldsCheck;
        }

        return validateBusinessRules(rawData);
    }

    private Map<String, Object> buildMetadata(HealthRawData rawData) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("receivedAt", OffsetDateTime.now().toString());
        metadata.put("source", getSource(rawData));

        List<String> presentFields = collectPresentFields(rawData);
        metadata.put("dataFieldsPresent", presentFields);
        metadata.put("isPartialData", presentFields.size() < 3);

        return metadata;
    }

    private String getSource(HealthRawData rawData) {
        return rawData.getSource() != null ? rawData.getSource() : "google";
    }

    private List<String> collectPresentFields(HealthRawData rawData) {
        List<String> presentFields = new ArrayList<>();
        if (rawData.getHeartRate() != null) {
            presentFields.add("heartRate");
        }
        if (rawData.getSteps() != null) {
            presentFields.add("steps");
        }
        if (rawData.getSleepHours() != null) {
            presentFields.add("sleepHours");
        }
        return presentFields;
    }

    private ValidationResult validateUserId(HealthRawData rawData) {
        String userId = rawData.getUserId();
        if (userId == null || userId.isBlank()) {
            log.warn("Validation failed: userId is null or blank");
            return ValidationResult.invalid("user_id_missing", "userId is missing or empty", rawData);
        }
        return ValidationResult.valid(null);
    }

    private ValidationResult validateDataFieldsPresent(HealthRawData rawData) {
        if (rawData.getHeartRate() == null && rawData.getSteps() == null && rawData.getSleepHours() == null) {
            return ValidationResult.invalid("no_data_fields",
                    "at least one data field (heartRate, steps, or sleepHours) must be present", rawData);
        }
        return ValidationResult.valid(null);
    }

    private ValidationResult validateBusinessRules(HealthRawData rawData) {
        ValidationResult heartRateCheck = validateHeartRate(rawData);
        if (!heartRateCheck.isValid()) {
            return heartRateCheck;
        }

        ValidationResult stepsCheck = validateSteps(rawData);
        if (!stepsCheck.isValid()) {
            return stepsCheck;
        }

        return validateSleepHours(rawData);
    }

    private ValidationResult validateHeartRate(HealthRawData rawData) {
        if (rawData.getHeartRate() == null) {
            return ValidationResult.valid(null);
        }

        int heartRate = rawData.getHeartRate();
        if (heartRate < minHeartRate || heartRate > maxHeartRate) {
            return ValidationResult.invalid("heart_rate_out_of_bounds",
                    String.format(
                            "heart rate %d out of range [%d..%d]", heartRate, minHeartRate, maxHeartRate), rawData);
        }
        return ValidationResult.valid(null);
    }

    private ValidationResult validateSteps(HealthRawData rawData) {
        if (rawData.getSteps() == null) {
            return ValidationResult.valid(null);
        }

        int steps = rawData.getSteps();
        if (steps < minSteps || steps > maxSteps) {
            return ValidationResult.invalid("steps_out_of_bounds",
                    String.format("steps %d out of allowed range [%d..%d]", steps, minSteps, maxSteps), rawData);
        }
        return ValidationResult.valid(null);
    }

    private ValidationResult validateSleepHours(HealthRawData rawData) {
        if (rawData.getSleepHours() == null) {
            return ValidationResult.valid(null);
        }

        double sleepHours = rawData.getSleepHours();
        if (sleepHours < minSleep || sleepHours > maxSleep) {
            return ValidationResult.invalid("sleep_out_of_bounds",
                    String.format(
                            "sleep %.2f out of allowed range [%.1f..%.1f]", sleepHours, minSleep, maxSleep), rawData);
        }
        return ValidationResult.valid(null);
    }
}
