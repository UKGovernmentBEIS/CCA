package uk.gov.cca.api.migration.underlyingagreement;

import java.util.Collections;
import java.util.List;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.migration.MigrationUtil;

@UtilityClass
public class UnderlyingAgreementMigrationUtil {
    
    public String constructSectionQuery(String query, List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(query);
        if(!accountIds.isEmpty()) {
            String inIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format(" and tu.[tu_id] in (%s)", inIdsSql));
        }
        return queryBuilder.toString();
    }
    
    public String constructErrorMessage(String targetUnitId, Long persistentAccountId, String errorMessage) {
        return "target_unit_id: " + MigrationUtil.convertCcaToLegacyBusinessId(targetUnitId) +
                " | persistent_id: " + persistentAccountId +
                " | Error: " + errorMessage;
    }
    
    public String constructErrorMessage(String targetUnitId, Long persistentAccountId, String errorMessage, String data) {
        return "target_unit_id: " + MigrationUtil.convertCcaToLegacyBusinessId(targetUnitId) +
                " | persistent_id: " + persistentAccountId +
                " | Error: " + errorMessage +
                " | data: " + data;
    }
    
    public String constructErrorMessage(String targetUnitId, Long persistentAccountId, String sectionName, String errorMessage, String data) {
        return "target_unit_id: " + MigrationUtil.convertCcaToLegacyBusinessId(targetUnitId) +
                " | persistent_id: " + persistentAccountId +
                " | section_name: " + sectionName +
                " | Error: " + errorMessage +
                " | data: " + data;
    }

    public String constructSuccessMessage(String targetUnitId, Long persistentAccountId) {
        return "target_unit_id: " + MigrationUtil.convertCcaToLegacyBusinessId(targetUnitId) +
                " | persistent_id: " + persistentAccountId;
    }
}
