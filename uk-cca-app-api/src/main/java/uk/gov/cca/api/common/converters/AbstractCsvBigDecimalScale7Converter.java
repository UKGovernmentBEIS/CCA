package uk.gov.cca.api.common.converters;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public abstract class AbstractCsvBigDecimalScale7Converter extends AbstractBeanField<BigDecimal, String> {

	protected abstract RoundingMode getRoundingMode();

	@Override
	protected BigDecimal convert(String value) throws CsvDataTypeMismatchException {
		try {
			return StringUtils.isBlank(value) ? null
					: new BigDecimal(value.replace("%", "").trim()).setScale(7, getRoundingMode());
		} catch (Exception e) {
			throw new CsvDataTypeMismatchException(e.getMessage());
		}
	}
}
