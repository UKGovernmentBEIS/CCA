package uk.gov.cca.api.migration.createsector.common;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.migration.createsector.cca2.SectorAssociationVO;

@UtilityClass
public class SectorAssociationMigrationHelper {
    
    public String constructQuery(String query, String acronyms) {
        return ObjectUtils.isEmpty(acronyms) ? query
                : String.format(query + " and sect_id IN (%s)", Arrays.stream(acronyms.split(","))
                        .filter(Objects::nonNull)
                        .map(acronym -> "'" + acronym.trim() + "'")
                        .collect(Collectors.joining(",")));
    }

    public static String createErrorMessageForDuplicateAcronym(List<SectorAssociationVO> sectorVOs) {
        final String duplicateAcronymErrorMsg = "Sector acronym already exists";
        return constructErrorMessage(sectorVOs.get(0), 
                duplicateAcronymErrorMsg, sectorVOs.size() + " acronyms, " + sectorVOs.get(0).getAcronym());
    }

    public static String constructErrorMessage(SectorAssociationSource sa, String errorMessage, String data) {
        return "acronym: " + sa.getAcronym() 
        + " | short_name: " + sa.getCommonName() 
        + " | legal_name: "  + sa.getLegalName() 
        + " | Error: " + errorMessage
        + " | data: " + data;
    }

    public static String constructSuccessMessage(SectorAssociationSource sa) {
        return "acronym: " + sa.getAcronym() 
        + " | short_name: " + sa.getCommonName() 
        + " | legal_name: " + sa.getLegalName();
    }

}
