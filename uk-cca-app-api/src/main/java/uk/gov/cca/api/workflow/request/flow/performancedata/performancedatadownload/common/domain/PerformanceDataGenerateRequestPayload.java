package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataGenerateRequestPayload extends CcaRequestPayload {

	private TargetPeriodDocumentTemplate targetPeriodDocument;

	private FileDTO template;

	private SectorAssociationInfo sectorAssociationInfo;

	private PerformanceDataTargetPeriodType targetPeriodType;

	private PerformanceDataSubmissionType submissionType;

	@Builder.Default
	private Map<Long, TargetUnitAccountReport> accountsReports = new HashMap<>();
}
