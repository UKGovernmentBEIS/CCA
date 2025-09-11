package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto;

import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPerformanceAccountTemplateDataReportInfoDTO {

	private TargetPeriodType targetPeriodType;

    private String targetPeriodName;
    
    private Year targetPeriodYear;
    
}
