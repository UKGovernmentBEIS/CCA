package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TargetUnitMoaMapperTest {

    private final TargetUnitMoaMapper mapper = Mappers.getMapper(TargetUnitMoaMapper.class);

    @Test
    void toGeneratedActionPayload() {
        final Long sectorAssociationId = 1L;
        final Year chargingYear = Year.of(2025);
        final FileInfoDTO sectorMoaDocument = FileInfoDTO.builder()
                .uuid("a2e9f302-8852-4951-b79a-bc9226a0cff5")
                .build();
        final String transactionId = "CCATM01295";

        TargetUnitMoaRequestPayload payload = TargetUnitMoaRequestPayload.builder()
                .sectorAssociationId(sectorAssociationId)
                .targetUnitMoaDocument(sectorMoaDocument)
                .build();

        TargetUnitMoaRequestMetadata metadata = TargetUnitMoaRequestMetadata.builder()
                .transactionId(transactionId)
                .businessId("ADS_1-T00012")
                .parentRequestId("S25145")
                .build();

        List<DefaultNoticeRecipient> recipients = List.of(DefaultNoticeRecipient.builder()
                        .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                        .email("sector@cca.com")
                        .name("Sector Contact")
                        .build(),
                DefaultNoticeRecipient.builder()
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .email("responsible-person@cca.com")
                        .name("Responsible Person")
                        .build(),
                DefaultNoticeRecipient.builder()
                        .recipientType(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .email("administrative-contact@cca.com")
                        .name("Administrative Contact")
                        .build());


        TargetUnitMoaGeneratedRequestActionPayload targetUnitMoaGeneratedRequestActionPayload = mapper.toGeneratedActionPayload(payload, metadata, chargingYear, recipients);

        assertThat(targetUnitMoaGeneratedRequestActionPayload.getTransactionId()).isEqualTo(transactionId);
        assertThat(targetUnitMoaGeneratedRequestActionPayload.getChargingYear()).isEqualTo(chargingYear);
        assertThat(targetUnitMoaGeneratedRequestActionPayload.getMoaDocument().getUuid()).isEqualTo(sectorMoaDocument.getUuid());
        assertThat(targetUnitMoaGeneratedRequestActionPayload.getRecipients()).isEqualTo(recipients);
    }
}
