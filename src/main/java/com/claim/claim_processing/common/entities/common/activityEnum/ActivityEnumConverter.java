package com.claim.claim_processing.common.entities.common.activityEnum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ActivityEnumConverter implements AttributeConverter<ActivityEnum, String> {

    @Override
    public String convertToDatabaseColumn(ActivityEnum attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public ActivityEnum convertToEntityAttribute(String dbData) {
        return dbData != null ? ActivityEnum.valueOf(dbData) : null;
    }
}
