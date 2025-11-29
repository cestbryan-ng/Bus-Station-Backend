package com.enspy26.gi.database_agence_voyage.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return (duration != null) ? duration.getSeconds() : null;
    }

    @Override
    public Duration convertToEntityAttribute(Long seconds) {
        return (seconds != null) ? Duration.ofSeconds(seconds) : null;
    }
}