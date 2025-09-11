package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataSubmissionType;
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorPerformanceAccountTemplateDataReportSearchCriteria {

	@Size(min = 3, max = 255)
	private String targetUnitAccountBusinessId;

	@NotNull
	private TargetPeriodType targetPeriodType;

	private PerformanceAccountTemplateDataStatus status;

	private PerformanceAccountTemplateDataSubmissionType submissionType;

	@Valid
	@NotNull
	@JsonUnwrapped
	private PagingRequest paging;

}
