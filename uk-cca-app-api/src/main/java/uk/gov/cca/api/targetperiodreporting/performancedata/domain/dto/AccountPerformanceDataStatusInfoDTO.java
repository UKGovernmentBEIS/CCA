package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPerformanceDataStatusInfoDTO {

    private TargetPeriodType targetPeriodType;

    private String targetPeriodName;

    private boolean locked;

    private int reportVersion;

    private boolean isEditable;
}
