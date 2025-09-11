package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.domain.UnderlyingAgreementVariationPeerReviewRequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class UnderlyingAgreementVariationApplicationPeerReviewMapperTest {

    private UnderlyingAgreementVariationApplicationPeerReviewMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(UnderlyingAgreementVariationApplicationPeerReviewMapper.class);
    }

    @Test
    void toUnderlyingAgreementVariationPeerReviewRequestTaskPayload() {
        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(Facility.builder()
                                        .status(FacilityStatus.NEW)
                                        .facilityItem(FacilityItem.builder().facilityId("1").build())
                                        .build()))
                                .build())
                        .build())
                .build();

        // invoke
        UnderlyingAgreementVariationPeerReviewRequestTaskPayload requestTaskPayload =
                mapper.toUnderlyingAgreementVariationPeerReviewRequestTaskPayload(requestPayload);

        // verify
        assertThat(requestTaskPayload).isNotNull();
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().size()).isEqualTo(1);
    }

    @Test
    void toUnderlyingAgreementReviewRequestTaskPayload() {
        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(Facility.builder()
                                        .status(FacilityStatus.NEW)
                                        .facilityItem(FacilityItem.builder().facilityId("1").build())
                                        .build()))
                                .build())
                        .build())
                .build();

        // invoke
        UnderlyingAgreementVariationReviewRequestTaskPayload requestTaskPayload = mapper.toUnderlyingAgreementVariationReviewRequestTaskPayload(requestPayload);

        // verify
        assertThat(requestTaskPayload).isNotNull();
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().size()).isEqualTo(1);
    }
}
