package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.handler;

import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

@Service
public class UnderlyingAgreementActivationInitializer implements InitializeRequestTaskHandler {
	
	@Override
    public RequestTaskPayload initializePayload(Request request) {
        return UnderlyingAgreementActivationRequestTaskPayload.builder()
        		.payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION_PAYLOAD)
        		.build();
    }
	@Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_ACTIVATION);
    }
}
