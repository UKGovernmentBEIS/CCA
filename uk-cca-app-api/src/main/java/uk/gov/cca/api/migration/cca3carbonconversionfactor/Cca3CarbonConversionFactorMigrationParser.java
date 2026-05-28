package uk.gov.cca.api.migration.cca3carbonconversionfactor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cca3CarbonConversionFactorMigrationParser {

	private static final String DELIMITER = ";";
    private static final int EXPECTED_COLUMNS = 2;
    
    public static Cca3CarbonConversionFactorMigrationParseResult parse(String input) {
    	Map<String, BigDecimal> validRecords = new HashMap<>();
        List<String> parsingErrors = new ArrayList<>();
        String[] records = input.split("\n");
        
		IntStream.range(0, records.length)
				.mapToObj(i -> new AbstractMap.SimpleEntry<>(i + 1, records[i].trim()))
				.filter(entry -> !entry.getValue().isEmpty()).forEach(entry -> {
					String line = entry.getValue();
					parseRecord(line, parsingErrors, validRecords);
				});
        
        return Cca3CarbonConversionFactorMigrationParseResult.builder()
                .parsedCarbonConversionFactorMap(validRecords)
                .totalRecords(records.length)
                .parsingErrors(parsingErrors)
                .build();
    }
    
    private static void parseRecord(String line, List<String> errors, Map<String, BigDecimal> validRecords) {
    	String[] columns = line.split(DELIMITER, -1);
        
        if (columns.length < EXPECTED_COLUMNS) {
            errors.add(String.format(
                    "Line '%s' has invalid format. Expecting %d semicolon separated columns but found %d",
                    line, EXPECTED_COLUMNS, columns.length
            ));
            return;
        }
        
        List<String> lineErrors = new ArrayList<>();
        String facilityBusinessId = parseFacilityBusinessId(columns[0], lineErrors);
        BigDecimal conversionFactor = parseBigDecimal(columns[1], lineErrors);
        
        if (lineErrors.isEmpty()) {
            // Duplicate check
            if (validRecords.containsKey(facilityBusinessId)) {
                lineErrors.add("Duplicate facilityBusinessId: " + facilityBusinessId);
            } else {
                validRecords.put(facilityBusinessId, conversionFactor);
            }
        }
        if (!lineErrors.isEmpty()) {
            errors.addAll(
                lineErrors.stream()
                    .map(error -> String.format("Parsing Error, '%s': %s", line, error))
                    .toList()
            );
        }
    }
    
    private static String parseFacilityBusinessId(String facilityBusinessId, List<String> errors) {
        if (StringUtils.isBlank(facilityBusinessId)) {
            errors.add("Facility ID cannot be empty");
            return null;
        }
        return facilityBusinessId.trim();
    }
    
    private static BigDecimal parseBigDecimal(String value, List<String> errors) {
        try {
        	if (StringUtils.isBlank(value)) {
                errors.add("Carbon conversion factor cannot be empty");
                return null;
            } else {
            	return new BigDecimal(value.trim()).setScale(7, RoundingMode.HALF_DOWN);
            }
        } catch (Exception e) {
            errors.add(String.format("Invalid number format for carbon conversion factor: '%s'", value));
            return null;
        }
    }

}
