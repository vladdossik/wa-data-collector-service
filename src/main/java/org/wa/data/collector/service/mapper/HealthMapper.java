package org.wa.data.collector.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.wa.data.collector.service.model.HealthRawData;
import org.wa.data.collector.service.model.HealthValidated;

@Mapper(componentModel = "spring")
public interface HealthMapper {
    
    @Mapping(target = "userId", source = "rawData.userId")
    @Mapping(target = "timestamp", source = "rawData.timestamp")
    @Mapping(target = "heartRate", source = "rawData.heartRate")
    @Mapping(target = "steps", source = "rawData.steps")
    @Mapping(target = "sleepHours", source = "rawData.sleepHours")
    @Mapping(target = "metadata", ignore = true)
    HealthValidated toHealthValidated(HealthRawData rawData);
}
