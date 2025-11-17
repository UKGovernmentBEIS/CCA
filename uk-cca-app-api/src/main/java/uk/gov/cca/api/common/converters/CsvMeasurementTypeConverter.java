package uk.gov.cca.api.common.converters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import org.apache.commons.lang3.StringUtils;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.util.Arrays;

public class CsvMeasurementTypeConverter extends AbstractBeanField<MeasurementType, String> {

    @Override
    protected MeasurementType convert(String value) throws CsvDataTypeMismatchException {
        if(StringUtils.isBlank(value)) {
            return null;
        }

        String tValue = value.trim();
        return Arrays.stream(MeasurementType.values())
                .filter(type -> type.getDescription().equalsIgnoreCase(tValue))
                .findFirst().orElseThrow(() -> new CsvDataTypeMismatchException("Failed to convert to measurement type '" + tValue + "'"));
    }
}
