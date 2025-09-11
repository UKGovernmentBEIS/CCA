package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceAccountTemplateDataProcessingRequestPayload extends CcaRequestPayload {
    
    private SectorAssociationInfo sectorAssociationInfo;
    private TargetPeriodType targetPeriodType;
    private Year targetPeriodYear;
    
    @Builder.Default
    private Map<Long, AccountUploadReport> accountFileReports = new HashMap<>();
    
}
