package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto;

import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPerformanceAccountTemplateDataReportDetailsDTO {
	
	private TargetPeriodType targetPeriodType;
    private String targetPeriodName;
    private Year targetPeriodYear;
    private PerformanceAccountTemplateDataContainer data;
    
}
