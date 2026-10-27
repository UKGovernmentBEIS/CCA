package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto;

import java.time.Year;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorPerformanceAccountTemplateDataReportSearchCriteria {

	@Size(min = 3, max = 255)
	private String term;

	@NotNull
	private Year targetPeriodYear;

	private PerformanceAccountTemplateDataStatus status;

	@Valid
	@NotNull
	@JsonUnwrapped
	private PagingRequest paging;

}
