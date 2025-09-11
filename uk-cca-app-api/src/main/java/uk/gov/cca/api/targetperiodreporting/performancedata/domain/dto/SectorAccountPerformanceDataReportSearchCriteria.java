package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAccountPerformanceDataReportSearchCriteria {

	private Long accountId;
	
	@Size(min = 3, max = 255)
	private String targetUnitAccountBusinessId;

	@NotNull
    private TargetPeriodType targetPeriodType;
    
    private TargetPeriodResultType performanceOutcome;
    
    private PerformanceDataSubmissionType submissionType;

    @Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
}
