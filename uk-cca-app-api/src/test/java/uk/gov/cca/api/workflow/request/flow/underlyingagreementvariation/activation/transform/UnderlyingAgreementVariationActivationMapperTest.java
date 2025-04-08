package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UnderlyingAgreementVariationActivationMapperTest {

    private UnderlyingAgreementVariationActivationMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(UnderlyingAgreementVariationActivationMapper.class);
    }

    @Test
    void toUnderlyingAgreementActivatedRequestActionPayload() {
        final Set<Facility> facilities = Set.of(Facility.builder()
                        .status(FacilityStatus.NEW)
                        .facilityItem(FacilityItem.builder().facilityId("ADS_53-F00001").build())
                        .build(),
                Facility.builder()
                        .status(FacilityStatus.NEW)
                        .facilityItem(FacilityItem.builder().facilityId("ADS_53-F00002").build())
                        .build());
        UnderlyingAgreementVariationPayload una = UnderlyingAgreementVariationPayload.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(facilities)
                        .targetPeriod5Details(TargetPeriod5Details.builder().exist(Boolean.FALSE).build())
                        .build())
                .underlyingAgreementTargetUnitDetails(UnderlyingAgreementTargetUnitDetails.builder().operatorName("name").build())
                .build();

        UnderlyingAgreementActivationDetails details = UnderlyingAgreementActivationDetails.builder().comments("comments").build();

        Map<UUID, String> attachments = Map.of(
                UUID.randomUUID(), "attachment1",
                UUID.randomUUID(), "attachment2"
        );

        Map<UUID, String> activationAttachments = Map.of(
                UUID.randomUUID(), "attachment3",
                UUID.randomUUID(), "attachment4"
        );

        Map<String, String> sectionsCompleted = Map.of(
                "targetPeriod5Details", "COMPLETED",
                "underlyingAgreementTargetUnitDetails", "IN PROGRESS",
                "ADS_53-F00001", "COMPLETED",
                "ADS_53-F00002", "COMPLETED"
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
                .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD)
                .underlyingAgreement(una)
                .businessId("ASD123")
                .facilitiesReviewGroupDecisions(Map.of(
                        "ADS_53-F00001", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .facilityStatus(FacilityStatus.NEW)
                                .type(CcaReviewDecisionType.ACCEPTED)
                                .build(),
                        "ADS_53-F00002", UnderlyingAgreementVariationFacilityReviewDecision.builder()
                                .type(CcaReviewDecisionType.REJECTED)
                                .facilityStatus(FacilityStatus.NEW)
                                .build()))
                .underlyingAgreementAttachments(attachments)
                .underlyingAgreementDocument(document)
                .sectionsCompleted(sectionsCompleted)
                .underlyingAgreementActivationDetails(details)
                .underlyingAgreementActivationAttachments(activationAttachments)
                .build();

        UnderlyingAgreementVariationActivatedRequestActionPayload actionPayload = mapper.toUnderlyingAgreementVariationActivatedRequestActionPayload(
                requestPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_PAYLOAD, usersInfo, defaultContacts);

        assertThat(actionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_PAYLOAD);
        assertThat(actionPayload.getUnderlyingAgreement()).isEqualTo(una);
        assertThat(actionPayload.getBusinessId()).isEqualTo("ASD123");
        assertThat(actionPayload.getUnderlyingAgreementActivationDetails()).isEqualTo(details);
        assertThat(actionPayload.getUsersInfo()).isEqualTo(usersInfo);
        assertThat(actionPayload.getDefaultContacts()).isEqualTo(defaultContacts);
        assertThat(actionPayload.getUnderlyingAgreementAttachments()).containsExactlyInAnyOrderEntriesOf(attachments);
        assertThat(actionPayload.getUnderlyingAgreementActivationAttachments()).containsExactlyInAnyOrderEntriesOf(activationAttachments);
        assertThat(actionPayload.getUnderlyingAgreementDocument()).isEqualTo(document);
        assertThat(actionPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(actionPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(actionPayload.getReviewAttachments()).isEmpty();
        assertThat(actionPayload.getSectionsCompleted()).isEqualTo(Map.of(
                "targetPeriod5Details", "COMPLETED",
                "underlyingAgreementTargetUnitDetails", "IN PROGRESS",
                "ADS_53-F00001", "COMPLETED",
                "ADS_53-F00002", "COMPLETED"
        ));
    }
}
