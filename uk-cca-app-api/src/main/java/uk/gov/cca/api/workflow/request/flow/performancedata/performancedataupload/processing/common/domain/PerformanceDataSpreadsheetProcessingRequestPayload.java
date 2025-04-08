package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDataSpreadsheetProcessingRequestPayload extends CcaRequestPayload {
    private Long accountId;
    private FileInfoDTO accountReportFile;
    private PerformanceData performanceData;
    private PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics;
}
