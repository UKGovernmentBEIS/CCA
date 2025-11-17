package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Cca3ErrorMessageUtil {

	public static String constructErrorMessage(Long row, String errorMessage) {
		return String.format("Row: %d | Error: %s", row, errorMessage);
	}

	public static String constructErrorMessage(String row, String errorMessage) {
		return String.format("Row: %s | Error: %s", row, errorMessage);
	}

	public static String constructErrorMessage(Long row, String errorMessage, String fieldName) {
		return String.format("Row: %d | Field name: %s | Error: %s", row, fieldName, errorMessage);
	}

	public static String constructSectorSubsectorErrorMessage(Long row, String errorMessage, String value) {
		return String.format("Row: %d | Error: %s: %s", row, errorMessage, value);
	}

	public static String constructSectorAndSubsectorCombinationError(String sectorSubsectorCombination) {
		return String.format("Sector and subsector combination \"%s\" is not unique", sectorSubsectorCombination);
	}

	public static String constructSectorDefinitionError(String sectorAcronym) {
		return String.format("Different sector definitions have been declared for the same sector \"%s\" ", sectorAcronym);
	}
}
