package uk.gov.cca.api.common.converters;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import uk.gov.cca.api.common.domain.AgreementType;

public class CsvAgreementTypeConverter extends AbstractBeanField<AgreementType, String>{

	@Override
    protected AgreementType convert(String value) throws CsvDataTypeMismatchException {
        if(StringUtils.isBlank(value)) {
            return null;
        }

        String tValue = value.trim();
        return Arrays.stream(AgreementType.values())
                .filter(type -> type.getDescription().equalsIgnoreCase(tValue))
                .findFirst().orElseThrow(() -> new CsvDataTypeMismatchException("Failed to convert to agreement type '" + tValue + "'"));
    }
}
