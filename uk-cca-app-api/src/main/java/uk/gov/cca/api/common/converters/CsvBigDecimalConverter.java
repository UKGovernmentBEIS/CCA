package uk.gov.cca.api.common.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CsvBigDecimalConverter extends AbstractBeanField<BigDecimal, String> {

    @Override
    protected BigDecimal convert(String value) throws CsvDataTypeMismatchException {
        try {
            return StringUtils.isBlank(value)
                    ? null
                    : new BigDecimal(value.trim()).setScale(7, RoundingMode.HALF_DOWN);
        } catch (Exception e) {
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }
}
