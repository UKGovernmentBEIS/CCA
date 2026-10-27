package uk.gov.cca.api.common.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import uk.gov.cca.api.common.utils.ConversionUtils;

import java.time.LocalDate;

public class CsvLocalDateConverter extends AbstractBeanField<LocalDate, String> {

    @Override
    protected LocalDate convert(String value) throws CsvDataTypeMismatchException {
        try {
            return ConversionUtils.toLocalDate(value);
        } catch (Exception e) {
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }
}
