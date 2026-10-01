package uk.gov.cca.api.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

@UtilityClass
public class ConversionUtils {

    private static final DateTimeFormatter FLEXIBLE_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("[dd/MM/yyyy]")
            .appendPattern("[d/M/yyyy]")
            .appendPattern("[dd-MM-yyyy]")
            .appendPattern("[d-M-yyyy]")
            .toFormatter();

    public BigDecimal toBigDecimal(String str) {
        return !StringUtils.isBlank(str) ? new BigDecimal(str.trim()) : null;
    }

    public LocalDate toLocalDate(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return LocalDate.parse(value.trim(), FLEXIBLE_DATE_FORMATTER);
    }

    public BigDecimal toBigDecimalScale7HalfDown(String value) {
        return StringUtils.isBlank(value) ? null
                : new BigDecimal(value.replace("%", "").trim()).setScale(7, RoundingMode.HALF_DOWN);
    }

    public String toStringTrim(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }
}

