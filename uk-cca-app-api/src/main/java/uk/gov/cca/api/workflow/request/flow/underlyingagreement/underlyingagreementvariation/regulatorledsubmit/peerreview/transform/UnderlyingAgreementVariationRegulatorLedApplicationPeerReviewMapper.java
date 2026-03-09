package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.domain.UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {CcaRequestTaskPayloadType.class})
public interface UnderlyingAgreementVariationRegulatorLedApplicationPeerReviewMapper {

    @Mapping(target = "payloadType", expression = "java(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW_PAYLOAD)")
    @Mapping(target = "determination", source = "payload.regulatorLedDetermination")
    @Mapping(target = "facilityChargeStartDateMap", source = "payload.regulatorLedFacilityChargeStartDateMap")
    @Mapping(target = "underlyingAgreement", source = "payload.underlyingAgreementProposed")
    UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload toUnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload(UnderlyingAgreementVariationRequestPayload payload);
}
