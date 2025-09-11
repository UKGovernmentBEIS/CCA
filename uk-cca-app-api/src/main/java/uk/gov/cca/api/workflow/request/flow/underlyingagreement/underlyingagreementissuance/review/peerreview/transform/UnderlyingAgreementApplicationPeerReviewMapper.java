package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.domain.UnderlyingAgreementPeerReviewRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface UnderlyingAgreementApplicationPeerReviewMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_PAYLOAD)")
    UnderlyingAgreementPeerReviewRequestTaskPayload toUnderlyingAgreementPeerReviewRequestTaskPayload(UnderlyingAgreementRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD)")
    UnderlyingAgreementReviewRequestTaskPayload toUnderlyingAgreementReviewRequestTaskPayload(UnderlyingAgreementRequestPayload requestPayload);
}
