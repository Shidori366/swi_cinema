package cz.swi.cinema.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class SQLiteLocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(LocalDateTime locDateTime) {
        return (locDateTime == null ? null : locDateTime.format(FORMATTER));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }
        // Handle cases where the DB might still have integer values from previous bugs
        if (sqlTimestamp.matches("\\d+")) {
            long epochMillis = Long.parseLong(sqlTimestamp);
            return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(epochMillis), java.time.ZoneId.systemDefault());
        }
        return LocalDateTime.parse(sqlTimestamp, FORMATTER);
    }
}