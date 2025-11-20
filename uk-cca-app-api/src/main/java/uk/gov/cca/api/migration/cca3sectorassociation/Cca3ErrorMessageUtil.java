package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Cca3ErrorMessageUtil {

	public String constructErrorMessage(Cca3SectorAssociationVO vo, String message) {
		return String.format("%s|%s : %s", vo.getSectorAcronym(), vo.getSubsectorName(), message);
	}

	public static String constructSectorAndSubsectorCombinationError(String sectorSubsectorCombination) {
		return String.format("Sector and subsector combination '%s' is not unique", sectorSubsectorCombination);
	}

	public static String constructSectorDefinitionError(String sectorAcronym) {
		return String.format("Different sector definitions have been declared for the same sector '%s'", sectorAcronym);
	}
}
