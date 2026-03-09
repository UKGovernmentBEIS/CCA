package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.transform.UnderlyingAgreementVariationRegulatorLedApplicationPeerReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class UnderlyingAgreementVariationRegulatorLedApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final UnderlyingAgreementVariationRegulatorLedApplicationPeerReviewMapper MAPPER = Mappers
            .getMapper(UnderlyingAgreementVariationRegulatorLedApplicationPeerReviewMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        return MAPPER.toUnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload(requestPayload);
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW);
    }
}
