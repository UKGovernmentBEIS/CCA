package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

public interface PerformanceDataSpreadsheetProcessingExtractDataService<T extends PerformanceData> {

    T extractData(Long accountId, PerformanceDataSpreadsheetProcessingRequestMetadata metadata, FileDTO file) throws Exception;

    PerformanceDataCalculatedMetrics extractCalculatedData(T performanceData);

    TargetPeriodDocumentTemplate getDocumentTemplateType();
}
