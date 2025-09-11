package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.handler;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.transform.UnderlyingAgreementApplicationPeerReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
public class UnderlyingAgreementApplicationPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final UnderlyingAgreementApplicationPeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementApplicationPeerReviewMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        return PEER_REVIEW_MAPPER.toUnderlyingAgreementPeerReviewRequestTaskPayload(requestPayload);
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW);
    }
}