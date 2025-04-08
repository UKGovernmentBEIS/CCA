package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestIdGenerator;

import java.util.List;

@Service
public class PerformanceDataSpreadsheetProcessingRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        final PerformanceDataSpreadsheetProcessingRequestMetadata metadata =
                (PerformanceDataSpreadsheetProcessingRequestMetadata) params.getRequestMetadata();

        final String accountAcronym = metadata.getAccountBusinessId();
        final String tp = metadata.getTargetPeriodDetails().getBusinessId().name();
        final int reportVersion = metadata.getReportVersion();

        return String.format("%s-%s-%s-V%d", accountAcronym, getPrefix(), tp, reportVersion);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.PERFORMANCE_DATA_SPREADSHEET_PROCESSING);
    }

    @Override
    public String getPrefix() {
        return "TPR";
    }
}
