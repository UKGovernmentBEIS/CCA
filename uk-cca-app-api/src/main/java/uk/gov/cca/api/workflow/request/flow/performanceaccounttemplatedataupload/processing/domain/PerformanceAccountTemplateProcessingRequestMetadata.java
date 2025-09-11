package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import java.time.Year;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceAccountTemplateProcessingRequestMetadata extends RequestMetadata {

	private String parentRequestId;
	
	private SectorAssociationInfo sectorAssociationInfo;
	private String sectorUserAssignee;
	
	private Long accountId;
	private String accountBusinessId;
	private TargetPeriodType targetPeriodType;
	private Year targetPeriodYear;
	private int reportVersion;
	
}
