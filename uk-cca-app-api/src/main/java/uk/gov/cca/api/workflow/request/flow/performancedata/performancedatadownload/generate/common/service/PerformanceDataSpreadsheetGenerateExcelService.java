package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

public interface PerformanceDataSpreadsheetGenerateExcelService {

    FileDTO generate(final PerformanceDataSpreadsheetGenerateRequestMetadata metadata, final Long accountId) throws Exception;

    TargetPeriodDocumentTemplate getTemplateType();
}
