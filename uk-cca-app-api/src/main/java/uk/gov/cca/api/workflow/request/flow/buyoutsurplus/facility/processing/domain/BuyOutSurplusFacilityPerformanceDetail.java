package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityPerformanceDetail {

	@NotBlank
	private String facilityBusinessId;
	
	@NotBlank
	private String siteName;
	
	@NotNull
	private TargetPeriodType targetPeriodType;
	
    @NotNull
    private Long performanceDataId;
	
	@NotNull
	@Positive
	private Integer performanceDataReportVersion;
	
	@NotNull
	private PerformanceDataSubmissionType submissionType;
	
    private String transactionCode;
    
	private @NotNull @Valid BuyOutSurplusCalculationDataContainer data;
    
    private boolean primaryRulesApplied;
}