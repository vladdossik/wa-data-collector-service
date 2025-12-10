package org.wa.data.collector.service.handler;

import lombok.Data;
import org.wa.data.collector.service.model.HealthRawData;
import org.wa.data.collector.service.model.HealthValidated;
import org.wa.data.collector.service.model.ValidationError;
import org.wa.data.collector.service.model.ValidationResult;

@Data
public class ProcessingContext {
    private HealthRawData rawData;
    private ValidationResult validationResult;
    private HealthValidated validatedData;
    private ValidationError error;
    private boolean shouldContinue = true;
    
    public static ProcessingContext fromRaw(HealthRawData raw) {
        ProcessingContext context = new ProcessingContext();
        context.setRawData(raw);
        return context;
    }
}
