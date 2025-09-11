package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.transform.UnderlyingAgreementVariationApplicationPeerReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class UnderlyingAgreementVariationWaitForPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final UnderlyingAgreementVariationApplicationPeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementVariationApplicationPeerReviewMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        return PEER_REVIEW_MAPPER.toUnderlyingAgreementVariationReviewRequestTaskPayload(requestPayload);
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_WAIT_FOR_PEER_REVIEW);
    }
}
