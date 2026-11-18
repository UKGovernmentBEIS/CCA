package uk.gov.cca.api.common.utils;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConversionUtils {

	public BigDecimal toBigDecimal(String str) {
		return !StringUtils.isBlank(str) ? new BigDecimal(str.trim()) : null;
	}
}

