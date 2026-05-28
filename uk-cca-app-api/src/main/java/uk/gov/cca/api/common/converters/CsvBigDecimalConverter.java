package uk.gov.cca.api.common.converters;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CsvBigDecimalConverter extends AbstractBeanField<BigDecimal, String> {

    @Override
    protected BigDecimal convert(String value) throws CsvDataTypeMismatchException {
        try {
            return StringUtils.isBlank(value)
                    ? null
                    : new BigDecimal(value.replace("%", "").trim());
        } catch (Exception e) {
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }
}
