package uk.gov.cca.api.migration.cca3carbonconversionfactor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cca3CarbonConversionFactorMigrationParseResult {

	private Map<String, BigDecimal> parsedCarbonConversionFactorMap;
    private int totalRecords;
    private List<String> parsingErrors;
}
