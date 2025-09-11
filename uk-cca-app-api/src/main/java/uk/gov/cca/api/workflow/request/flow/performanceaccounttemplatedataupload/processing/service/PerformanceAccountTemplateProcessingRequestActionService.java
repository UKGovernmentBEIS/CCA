package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.transform.PerformanceAccountTemplateProcessingMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingRequestActionService {
    
    private final RequestService requestService;
    
    private static final PerformanceAccountTemplateProcessingMapper MAPPER = Mappers
            .getMapper(PerformanceAccountTemplateProcessingMapper.class);
    
    public void addSubmittedAction(Request request, PerformanceAccountTemplateProcessingRequestMetadata metadata,
                                   PerformanceAccountTemplateDataContainer data) {
        final PerformanceAccountTemplateProcessingSubmittedRequestActionPayload requestActionPayload = MAPPER
                .toSubmittedAction(metadata, data);
        
        requestService.addActionToRequest(request, requestActionPayload,
                CcaRequestActionType.PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING_SUBMITTED,
                metadata.getSectorUserAssignee());
    }
}
