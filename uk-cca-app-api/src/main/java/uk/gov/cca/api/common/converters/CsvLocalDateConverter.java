package uk.gov.cca.api.common.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class CsvLocalDateConverter extends AbstractBeanField<LocalDate, String> {

    private static final DateTimeFormatter FLEXIBLE_DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("[dd/MM/yyyy]")
            .appendPattern("[d/M/yyyy]")
            .appendPattern("[dd-MM-yyyy]")
            .appendPattern("[d-M-yyyy]")
            .toFormatter();

    @Override
    protected LocalDate convert(String value) throws CsvDataTypeMismatchException {
        if(StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return LocalDate.parse(value.trim(), FLEXIBLE_DATE_FORMATTER);
        } catch (Exception e) {
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }
}
