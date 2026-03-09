package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.transform.UnderlyingAgreementVariationRegulatorLedSubmitMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class UnderlyingAgreementVariationRegulatorLedWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final UnderlyingAgreementVariationRegulatorLedSubmitMapper MAPPER = Mappers
            .getMapper(UnderlyingAgreementVariationRegulatorLedSubmitMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        return MAPPER.toUnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload(
                CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD, requestPayload);
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW);
    }
}
