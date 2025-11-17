package uk.gov.cca.api.common.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

public class CsvBooleanConverter extends AbstractBeanField<Boolean, String> {

    @Override
    protected Boolean convert(String value) throws CsvDataTypeMismatchException {
        if(StringUtils.isBlank(value)) {
            return null;
        }

        if(value.trim().equalsIgnoreCase("YES")) {
            return Boolean.TRUE;
        }

        if(value.trim().equalsIgnoreCase("NO")) {
            return Boolean.FALSE;
        }

        throw new CsvDataTypeMismatchException("Failed to convert to boolean value '" + value + "'");
    }
}
