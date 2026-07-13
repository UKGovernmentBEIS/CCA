package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorFacilityPerformanceDataReportSearchCriteria {
	
	@Size(min = 3, max = 255)
	private String facilityOrTargetUnitAccountBusinessId;

	@NotNull
    private TargetPeriodType targetPeriodType;
	
	@NotNull
	private PerformanceDataReportType targetPeriodReportType;
    
    private PerformanceDataFacilityTargetPeriodResultType reportStatus;
    
    private PerformanceDataSubmissionType subType;

    @Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
}
