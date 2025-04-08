package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestIdGenerator;

@Service
public class PerformanceAccountTemplateProcessingRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        final PerformanceAccountTemplateProcessingRequestMetadata metadata =
                (PerformanceAccountTemplateProcessingRequestMetadata) params.getRequestMetadata();

        return String.format("%s-%s-%s-%d-V%d", 
        		metadata.getAccountBusinessId(), 
        		getPrefix(), 
        		metadata.getTargetPeriodType().name(), 
        		metadata.getTargetPeriodYear().getValue(),
        		metadata.getReportVersion());
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING);
    }

    @Override
    public String getPrefix() {
        return "PAT";
    }
}
