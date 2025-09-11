package uk.gov.cca.api.migration.account;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TargetUnitAccountHelper {

    public String constructQuery(String query, String ids) {
        return ObjectUtils.isEmpty(ids) ? query
                : query + String.format(" and tu_id IN (%s)", 
                        Arrays.stream(ids.split(","))
                        .filter(Objects::nonNull)
                        .map(id -> "'" + id.trim() + "'")
                        .collect(Collectors.joining(",")));
    }
    
    public String constructSuccessMessage(TargetUnitAccountVO targetUnit) {
        return "target_unit_id: " + targetUnit.getTargetUnitId()
        + " | operator_name: " + targetUnit.getOperatorName()
        + " | sector_acronym: " + targetUnit.getSectorAcronym()
        + " | sub_sector_name: " + targetUnit.getSubsectorName();
    }

    public String constructErrorMessage(TargetUnitAccountVO targetUnit, String errorMessage, String data) {
        return "target_unit_id: " + targetUnit.getTargetUnitId()
        + " | operator_name: " + targetUnit.getOperatorName()
        + " | sector_acronym: " + targetUnit.getSectorAcronym()
        + " | sub_sector_name: " + targetUnit.getSubsectorName()
        + " | Error: " + errorMessage
        + " | data: " + data;
    }

}
