package uk.gov.cca.api.common.converters;

import java.math.RoundingMode;

public class CsvBigDecimalScale7HalfUpConverter extends AbstractCsvBigDecimalScale7Converter {

	@Override
    protected RoundingMode getRoundingMode() {
        return RoundingMode.HALF_UP;
    }
}
