package org.wa.data.collector.service.model;

import lombok.Getter;

@Getter
public class ValidationResult {
    private final boolean valid;
    private final HealthValidated validated;
    private final String errorCode;
    private final String message;
    private final Object rawPayload;

    private ValidationResult(boolean valid, HealthValidated validated, String errorCode, String message, Object rawPayload) {
        this.valid = valid;
        this.validated = validated;
        this.errorCode = errorCode;
        this.message = message;
        this.rawPayload = rawPayload;
    }

    public static ValidationResult valid(HealthValidated validatedData) {
        return new ValidationResult(true, validatedData, null, null, null);
    }

    public static ValidationResult invalid(String code, String msg, Object raw) {
        return new ValidationResult(false, null, code, msg, raw);
    }

}
