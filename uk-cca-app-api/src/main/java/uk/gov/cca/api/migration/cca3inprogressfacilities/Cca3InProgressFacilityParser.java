package uk.gov.cca.api.migration.cca3inprogressfacilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.tika.utils.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cca3InProgressFacilityParser {
    
    private static final String DELIMITER = "[;]";
    private static final int EXPECTED_COLUMNS = 12;
    
    public static Cca3InProgressFacilityMigrationParseResult parse(String input) {
        List<Cca3InProgressFacilityVO> validRecords = new ArrayList<>();
        List<String> parsingErrors = new ArrayList<>();
        String[] records = input.split("\n");
        
		IntStream.range(0, records.length)
				.mapToObj(i -> new AbstractMap.SimpleEntry<>(i + 1, records[i].trim()))
				.filter(entry -> !entry.getValue().isEmpty()).forEach(entry -> {
					int rowNumber = entry.getKey();
					String line = entry.getValue();
					parseRecord(line, rowNumber, parsingErrors, validRecords);
				});
        
        return Cca3InProgressFacilityMigrationParseResult.builder()
                .successfullyParsedInProgressFacilities(validRecords)
                .totalRecordsRead(records.length)
                .parsingErrors(parsingErrors)
                .build();
    }
    
    private static void parseRecord(String line,
    								int rowNumber,
                                    List<String> errors,
                                    List<Cca3InProgressFacilityVO> validRecords) {
        String[] columns = line.split(DELIMITER, -1);
        
        if (columns.length < EXPECTED_COLUMNS) {
            errors.add(String.format(
                    "Line '%s' has invalid format. Expecting %d pipe-separated columns but found %d",
                    line, EXPECTED_COLUMNS, columns.length
            ));
            return;
        }
        
        List<String> lineErrors = new ArrayList<>();
        
        String accountBusinessId = parseString(columns[0]);
        String facilityBusinessId = parseString(columns[1]);
        String facilityName = parseString(columns[2]);
        ApplicationReasonType applicationReasonType = parseApplicationReason(columns[3], lineErrors);
        Boolean isParticipatingInCca3 = parseBoolean(columns[4], lineErrors);
        
        BigDecimal tp7 = parseBigDecimal(columns[5], "TP7", lineErrors);
        BigDecimal tp8 = parseBigDecimal(columns[6], "TP8", lineErrors);
        BigDecimal tp9 = parseBigDecimal(columns[7], "TP9", lineErrors);
        
        BigDecimal totalFixedEnergy = parseBigDecimal(columns[8], "totalFixedEnergy", lineErrors);
        BigDecimal baselineVariableEnergy = parseBigDecimal(columns[9], "baselineVariableEnergy", lineErrors);
        BigDecimal totalThroughput = parseBigDecimal(columns[10], "totalThroughput", lineErrors);
        String throughputUnit = parseString(columns[11]);
        
        if (lineErrors.isEmpty()) {
            validRecords.add(Cca3InProgressFacilityVO.builder()
            		.rowNumber(rowNumber)
                    .targetUnitId(accountBusinessId)
                    .facilityId(facilityBusinessId)
                    .facilityName(facilityName)
                    .applicationReason(applicationReasonType)
                    .participatingInCca3Question(isParticipatingInCca3)
                    .participatingSchemeVersions(computeSchemeVersions(applicationReasonType, isParticipatingInCca3))
                    .participatingInCca3SchemeIndicator(computeSchemeVersions(applicationReasonType, isParticipatingInCca3).contains(SchemeVersion.CCA_3))
                    .tp7Improvement(tp7)
                    .tp8Improvement(tp8)
                    .tp9Improvement(tp9)
                    .totalFixedEnergy(totalFixedEnergy)
                    .baselineVariableEnergy(baselineVariableEnergy)
                    .totalThroughput(totalThroughput)
                    .throughputUnit(throughputUnit)
                    .build()
            );
        } else {
            errors.addAll(lineErrors.stream()
                    .map(error -> String.format("Row: %d | Parsing Error: %s", rowNumber, error))
                    .toList());
        }
    }

    private static String parseString(String field) {
        return StringUtils.isBlank(field) ? null : field.trim();
    }
    
    private static ApplicationReasonType parseApplicationReason(String reason, List<String> errors) {
    	if (StringUtils.isEmpty(reason)) {
            return null;
        }
        try {
            return ApplicationReasonType.valueOf(reason);
        } catch (IllegalArgumentException e) {
            errors.add(String.format("Invalid Application Reason: %s", reason));
            return null;
        }
    }
    
    private static BigDecimal parseBigDecimal(String value, String fieldName, List<String> errors) {
        try {
        	return StringUtils.isBlank(value)
                    ? null
                    : new BigDecimal(value.trim()).setScale(7, RoundingMode.HALF_DOWN);
        } catch (Exception e) {
            errors.add(String.format("Invalid number format for %s: '%s'", fieldName, value));
            return null;
        }
    }

    private static Boolean parseBoolean(String value, List<String> errors) {
    	if(StringUtils.isBlank(value)) {
    		return null;
    	}
        return switch (value.trim().toUpperCase()) {
            case "YES" -> true;
            case "NO" -> false;
            default -> {
                errors.add(String.format("Invalid boolean value '%s'", value));
                yield null;
            }
        };
    }
    
    private static Set<SchemeVersion> computeSchemeVersions(ApplicationReasonType applicationReasonType, Boolean participatingInCca3) {
    	if(applicationReasonType == null) {
    		return Set.of();
    	}
    	
		switch (applicationReasonType) {
		case NEW_AGREEMENT:
			return Set.of(SchemeVersion.CCA_3);

		case CHANGE_OF_OWNERSHIP:
			if (Boolean.TRUE.equals(participatingInCca3)) {
				return Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3);
			}
			if (Boolean.FALSE.equals(participatingInCca3)) {
				return Set.of(SchemeVersion.CCA_2);
			}
			return Set.of();

		default:
			return Set.of();
		}
	}
}
