package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@AllArgsConstructor
public class NonComplianceAppealOutcomeSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return NonComplianceAppealOutcomeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMIT_PAYLOAD)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMIT);
    }
}