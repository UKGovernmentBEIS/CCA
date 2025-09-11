package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.domain.UnderlyingAgreementVariationPeerReviewRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestTaskPayloadType.class})
public interface UnderlyingAgreementVariationApplicationPeerReviewMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW_PAYLOAD)")
    UnderlyingAgreementVariationPeerReviewRequestTaskPayload toUnderlyingAgreementVariationPeerReviewRequestTaskPayload(UnderlyingAgreementVariationRequestPayload requestPayload);

    @Mapping(target = "payloadType", expression = "java(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD)")
    UnderlyingAgreementVariationReviewRequestTaskPayload toUnderlyingAgreementVariationReviewRequestTaskPayload(UnderlyingAgreementVariationRequestPayload requestPayload);
}
