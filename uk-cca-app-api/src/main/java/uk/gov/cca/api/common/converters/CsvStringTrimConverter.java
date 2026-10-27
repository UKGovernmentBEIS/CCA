package uk.gov.cca.api.common.converters;

import com.opencsv.bean.AbstractBeanField;
import uk.gov.cca.api.common.utils.ConversionUtils;

public class CsvStringTrimConverter extends AbstractBeanField<String, String> {

    @Override
    protected String convert(String value) {
        return ConversionUtils.toStringTrim(value);
    }
}
