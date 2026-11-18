package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.account.domain.dto;

import java.time.LocalDateTime;
import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.account.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.account.domain.PerformanceAccountTemplateDataSubmissionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorPerformanceAccountTemplateDataReportItemDTO {

	private Long accountId;
	private String targetUnitAccountBusinessId;
	private String operatorName;
	private LocalDateTime submissionDate;
	private TargetPeriodType targetPeriodType;
	private Year targetPeriodYear;
	private PerformanceAccountTemplateDataStatus status;
	private PerformanceAccountTemplateDataSubmissionType submissionType;
	
	public SectorPerformanceAccountTemplateDataReportItemDTO(Long accountId, String targetUnitAccountBusinessId,
			String operatorName, LocalDateTime submissionDate, TargetPeriodType targetPeriodType, Year targetPeriodYear,
			String status, PerformanceAccountTemplateDataSubmissionType submissionType) {
		this.accountId = accountId;
		this.targetUnitAccountBusinessId = targetUnitAccountBusinessId;
		this.operatorName = operatorName;
		this.submissionDate = submissionDate;
		this.targetPeriodType = targetPeriodType;
		this.targetPeriodYear = targetPeriodYear;
		this.status = PerformanceAccountTemplateDataStatus.valueOf(status);
		this.submissionType = submissionType;
	}
	
	

}
