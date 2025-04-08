package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAccountsPerformanceReportItemDTO {
	private Long accountId;
	private String targetUnitBusinessId;
	private String operatorName;
	private LocalDateTime submissionDate;
	private Integer reportVersion;
	private TargetPeriodResultType performanceOutcome;
	private PerformanceDataSubmissionType submissionType;
	private boolean locked;
}
