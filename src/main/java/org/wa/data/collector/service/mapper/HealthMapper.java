package org.wa.data.collector.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wa.data.collector.service.model.HealthRawData;
import org.wa.data.collector.service.model.HealthValidated;

@Mapper(componentModel = "spring")
public interface HealthMapper {
    
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "timestamp", source = "timestamp")
    @Mapping(target = "heartRate", source = "heartRate")
    @Mapping(target = "steps", source = "steps")
    @Mapping(target = "sleepHours", source = "sleepHours")
    @Mapping(target = "metadata", ignore = true)
    HealthValidated toHealthValidated(HealthRawData rawData);
}
