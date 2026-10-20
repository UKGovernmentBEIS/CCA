package uk.gov.cca.api.common.converters;

import com.opencsv.bean.AbstractBeanField;
import org.apache.commons.lang3.StringUtils;

public class CsvStringTrimConverter extends AbstractBeanField<String, String> {

    @Override
    protected String convert(String value) {
        if(StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }
}
