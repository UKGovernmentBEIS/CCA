package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UnderlyingAgreementVariationReviewMapperTest {

    private UnderlyingAgreementVariationReviewMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(UnderlyingAgreementVariationReviewMapper.class);
    }

    @Test
    void toUnderlyingAgreementReviewRequestTaskPayload() {
        UnderlyingAgreementVariationPayload una = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(Boolean.FALSE).build())
                        .build())
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder().build())
                .build();

        Map<UUID, String> attachments = Map.of(
                UUID.randomUUID(), "attachment1",
                UUID.randomUUID(), "attachment2"
        );

        Map<String, String> sectionsCompleted = Map.of(
                "targetPeriod5Details", "COMPLETED",
                "underlyingAgreementTargetUnitDetails", "IN PROGRESS"
        );

        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
                .underlyingAgreement(una)
                .underlyingAgreementAttachments(attachments)
                .sectionsCompleted(sectionsCompleted)
                .build();

        UnderlyingAgreementVariationReviewRequestTaskPayload reviewTaskPayload = mapper.toUnderlyingAgreementVariationReviewRequestTaskPayload(
                requestPayload, CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD);

        assertThat(reviewTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD);
        assertThat(reviewTaskPayload.getUnderlyingAgreement()).isEqualTo(una);
        assertThat(reviewTaskPayload.getUnderlyingAgreementAttachments()).containsExactlyInAnyOrderEntriesOf(attachments);
        assertThat(reviewTaskPayload.getSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(sectionsCompleted);
        assertThat(reviewTaskPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(reviewTaskPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(reviewTaskPayload.getReviewAttachments()).isEmpty();
    }
    
    @Test
    void toUnderlyingAgreementVariationAcceptedRequestActionPayload() {
        UnderlyingAgreementVariationPayload una = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(Boolean.FALSE).build())
                        .build())
            	.underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
            	.underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder().reason("reason").build())
                .build();

        Map<UUID, String> attachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );

        Map<String, String> sectionsCompleted = Map.of(
            "targetPeriod5Details", "COMPLETED",
            "underlyingAgreementTargetUnitDetails", "IN PROGRESS",
            "underlyingAgreementVariationDetails", "IN PROGRESS"
        );
        
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "sector", RequestActionUserInfo.builder().name("Sector").roleCode("sector_user_administrator").build()
        );
        List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        UUID uuid = UUID.randomUUID();
        FileInfoDTO document = FileInfoDTO.builder()
                .name("una.pdf")
                .uuid(uuid.toString())
                .build();
        
        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
            .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
            .underlyingAgreement(una)
            .businessId("ASD123")
            .underlyingAgreementAttachments(attachments)
            .underlyingAgreementDocument(document)
            .sectionsCompleted(sectionsCompleted)
            .build();

        UnderlyingAgreementVariationAcceptedRequestActionPayload actionPayload = mapper.toUnderlyingAgreementVariationAcceptedRequestActionPayload(
            requestPayload, usersInfo, defaultContacts);

        assertThat(actionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PAYLOAD);
        assertThat(actionPayload.getUnderlyingAgreement()).isEqualTo(una);
        assertThat(actionPayload.getBusinessId()).isEqualTo("ASD123");
        assertThat(actionPayload.getUsersInfo()).isEqualTo(usersInfo);
        assertThat(actionPayload.getDefaultContacts()).isEqualTo(defaultContacts);
        assertThat(actionPayload.getUnderlyingAgreementAttachments()).containsExactlyInAnyOrderEntriesOf(attachments);
        assertThat(actionPayload.getUnderlyingAgreementDocument()).isEqualTo(document);
        assertThat(actionPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(actionPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(actionPayload.getReviewAttachments()).isEmpty();
    }

    @Test
    void toUnderlyingAgreementVariationRejectedRequestActionPayload() {
        UnderlyingAgreementVariationPayload una = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(Boolean.FALSE).build())
                        .build())
            	.underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
            	.underlyingAgreementVariationDetails(UnderlyingAgreementVariationDetails.builder().reason("reason").build())
                .build();

        Map<UUID, String> attachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );

        Map<String, String> sectionsCompleted = Map.of(
            "targetPeriod5Details", "COMPLETED",
            "underlyingAgreementTargetUnitDetails", "IN PROGRESS",
            "underlyingAgreementVariationDetails", "IN PROGRESS"
        );

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "sector", RequestActionUserInfo.builder().name("Sector").roleCode("sector_user_administrator").build()
        );
        List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );

        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
            .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
            .underlyingAgreement(una)
            .businessId("ASD123")
            .underlyingAgreementAttachments(attachments)
            .sectionsCompleted(sectionsCompleted)
            .build();

        UnderlyingAgreementVariationRejectedRequestActionPayload actionPayload = mapper.toUnderlyingAgreementVariationRejectedRequestActionPayload(
            requestPayload, usersInfo, defaultContacts);

        assertThat(actionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REJECTED_PAYLOAD);
        assertThat(actionPayload.getUnderlyingAgreement()).isEqualTo(una);
        assertThat(actionPayload.getBusinessId()).isEqualTo("ASD123");
        assertThat(actionPayload.getUsersInfo()).isEqualTo(usersInfo);
        assertThat(actionPayload.getDefaultContacts()).isEqualTo(defaultContacts);
        assertThat(actionPayload.getUnderlyingAgreementAttachments()).containsExactlyInAnyOrderEntriesOf(attachments);
        assertThat(actionPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(actionPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(actionPayload.getReviewAttachments()).isEmpty();
    }
}
