package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.domain.UnderlyingAgreementActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

class UnderlyingAgreementActivationMapperTest {

	private UnderlyingAgreementActivationMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(UnderlyingAgreementActivationMapper.class);
    }
    
    @Test
    void toUnderlyingAgreementActivatedRequestActionPayload() {
        final UnderlyingAgreementActivationDetails details = UnderlyingAgreementActivationDetails.builder()
                .comments("comments")
                .build();
        final Map<UUID, String> activationAttachments = Map.of(
                UUID.randomUUID(), "attachment3",
                UUID.randomUUID(), "attachment4"
        );
        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "sector", RequestActionUserInfo.builder().name("Sector").roleCode("sector_user_administrator").build()
        );
        final List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final FileInfoDTO document = FileInfoDTO.builder()
                .name("una.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        final Map<SchemeVersion, FileInfoDTO> documentMap = Map.of(SchemeVersion.CCA_2, document);
        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
            .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD)
            .businessId("ASD123")
            .underlyingAgreementDocuments(documentMap)
            .underlyingAgreementActivationDetails(details)
            .underlyingAgreementActivationAttachments(activationAttachments)
            .build();

        // Invoke
        UnderlyingAgreementActivatedRequestActionPayload actionPayload = mapper.toUnderlyingAgreementActivatedRequestActionPayload(
            requestPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_ACTIVATED_PAYLOAD, usersInfo, defaultContacts);

        // Verify
        assertThat(actionPayload.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_ACTIVATED_PAYLOAD);
        assertThat(actionPayload.getBusinessId()).isEqualTo("ASD123");
        assertThat(actionPayload.getUnderlyingAgreementActivationDetails()).isEqualTo(details);
        assertThat(actionPayload.getUsersInfo()).isEqualTo(usersInfo);
        assertThat(actionPayload.getDefaultContacts()).isEqualTo(defaultContacts);
        assertThat(actionPayload.getUnderlyingAgreementActivationAttachments()).containsExactlyInAnyOrderEntriesOf(activationAttachments);
        assertThat(actionPayload.getUnderlyingAgreementDocuments()).isEqualTo(documentMap);
    }
}
