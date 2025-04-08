package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.Year;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectorMoaMapperTest {

    private final SectorMoaMapper mapper = Mappers.getMapper(SectorMoaMapper.class);

    @Test
    void toGeneratedActionPayload() {
        final Long sectorAssociationId = 1L;
        final Year chargingYear = Year.of(2025);
        final FileInfoDTO sectorMoaDocument = FileInfoDTO.builder()
                .uuid("a2e9f302-8852-4951-b79a-bc9226a0cff5")
                .build();
        final String transactionId = "CCACM01295";

        SectorMoaRequestPayload payload = SectorMoaRequestPayload.builder()
                .sectorAssociationId(sectorAssociationId)
                .sectorMoaDocument(sectorMoaDocument)
                .build();

        SectorMoaRequestMetadata metadata = SectorMoaRequestMetadata.builder()
                .transactionId("CCACM01295")
                .sectorAcronym("AIC")
                .parentRequestId("S25145")
                .build();

        List<DefaultNoticeRecipient> recipients = List.of(DefaultNoticeRecipient.builder()
                .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                .email("email")
                .name("Sector Contact Name")
                .build());


        SectorMoaGeneratedRequestActionPayload sectorMoaGeneratedActionPayload = mapper.toGeneratedActionPayload(payload, metadata, chargingYear, recipients);

        assertThat(sectorMoaGeneratedActionPayload.getTransactionId()).isEqualTo(transactionId);
        assertThat(sectorMoaGeneratedActionPayload.getChargingYear()).isEqualTo(chargingYear);
        assertThat(sectorMoaGeneratedActionPayload.getMoaDocument().getUuid()).isEqualTo(sectorMoaDocument.getUuid());
        assertThat(sectorMoaGeneratedActionPayload.getRecipients()).isEqualTo(recipients);
    }
}
