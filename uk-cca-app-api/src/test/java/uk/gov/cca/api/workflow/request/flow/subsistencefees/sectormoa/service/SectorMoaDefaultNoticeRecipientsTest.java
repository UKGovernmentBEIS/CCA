package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorMoaDefaultNoticeRecipientsTest {

    @InjectMocks
    private SectorMoaDefaultNoticeRecipients service;

    @Mock
    private SectorAssociationInfoService sectorAssociationInfoService;

    @Test
    void getRecipients() {
        final Long sectorAssociationId = 1L;

        final Request request = Request.builder()
                .payload(SectorMoaRequestPayload.builder()
                        .sectorAssociationId(sectorAssociationId)
                        .sectorMoaDocument(FileInfoDTO.builder()
                                .uuid("a2e9f302-8852-4951-b79a-bc9226a0cff5")
                                .build())
                        .build())
                .build();

        final SectorAssociationContactDTO sectorAssociationContactDTO = SectorAssociationContactDTO.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .build();

        List<DefaultNoticeRecipient> expectedRecipients = List.of(DefaultNoticeRecipient.builder()
                .name(sectorAssociationContactDTO.getFullName())
                .email(sectorAssociationContactDTO.getEmail())
                .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                .build());

        when(sectorAssociationInfoService.getSectorAssociationContact(sectorAssociationId)).thenReturn(sectorAssociationContactDTO);

        List<DefaultNoticeRecipient> actualRecipients = service.getRecipients(request);

        assertThat(actualRecipients).isEqualTo(expectedRecipients);
        verify(sectorAssociationInfoService, times(1)).getSectorAssociationContact(sectorAssociationId);
    }

    @Test
    void getType() {
        assertThat(service.getType())
                .isEqualTo(CcaRequestType.SECTOR_MOA);
    }
}
