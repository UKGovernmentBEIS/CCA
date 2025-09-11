package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.peerreview.domain.UnderlyingAgreementPeerReviewRequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementApplicationPeerReviewMapperTest {

    private UnderlyingAgreementApplicationPeerReviewMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(UnderlyingAgreementApplicationPeerReviewMapper.class);
    }

    @Test
    void toUnderlyingAgreementPeerReviewRequestTaskPayload() {
        UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(Facility.builder()
                                        .status(FacilityStatus.NEW)
                                        .facilityItem(FacilityItem.builder().facilityId("1").build())
                                        .build()))
                                .build())
                        .build())
                .build();

        // invoke
        UnderlyingAgreementPeerReviewRequestTaskPayload requestTaskPayload =
                mapper.toUnderlyingAgreementPeerReviewRequestTaskPayload(requestPayload);

        // verify
        assertThat(requestTaskPayload).isNotNull();
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().size()).isEqualTo(1);
    }

    @Test
    void toUnderlyingAgreementReviewRequestTaskPayload() {
        UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(Facility.builder()
                                        .status(FacilityStatus.NEW)
                                        .facilityItem(FacilityItem.builder().facilityId("1").build())
                                        .build()))
                                .build())
                        .build())
                .build();

        // invoke
        UnderlyingAgreementReviewRequestTaskPayload requestTaskPayload = mapper.toUnderlyingAgreementReviewRequestTaskPayload(requestPayload);

        // verify
        assertThat(requestTaskPayload).isNotNull();
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_WAIT_FOR_PEER_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().size()).isEqualTo(1);
    }

}
