package uk.gov.cca.api.migration.sectoruser;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SectorUserInvitationHelper {
    
    public static String constructErrorMessage(Long rowId, String errorMessage) {
        return "Row: " + rowId + " | Error: " + errorMessage;
    }

}
