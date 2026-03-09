package uk.gov.cca.api.workflow.request.flow.noncompliance.details.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceInfoService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@AllArgsConstructor
public class NonComplianceDetailsSubmitInitializer implements InitializeRequestTaskHandler {

    private final NonComplianceInfoService nonComplianceInfoService;

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final Long accountId = request.getAccountId();
        return NonComplianceDetailsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD)
                .allRelevantWorkflows(nonComplianceInfoService.getAllRelevantWorkflows(accountId, request.getId()))
                .allRelevantFacilities(nonComplianceInfoService.getAllRelevantFacilities(accountId))
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.NON_COMPLIANCE_DETAILS_SUBMIT);
    }
}
