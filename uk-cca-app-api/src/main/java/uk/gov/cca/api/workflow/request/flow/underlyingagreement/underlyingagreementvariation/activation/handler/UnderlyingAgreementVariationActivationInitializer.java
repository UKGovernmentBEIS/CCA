package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.handler;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class UnderlyingAgreementVariationActivationInitializer implements InitializeRequestTaskHandler {
    @Override
    public RequestTaskPayload initializePayload(Request request) {
        return UnderlyingAgreementVariationActivationRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_PAYLOAD)
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION);
    }
}
