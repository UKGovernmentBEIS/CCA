package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SectorMoaOfficialNoticeServiceTest {

    @InjectMocks
    private SectorMoaOfficialNoticeService sectorMoaOfficialNoticeService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void sendOfficialNotice() {
        final Long sectorAssociationId = 1L;
        final FileInfoDTO document = FileInfoDTO.builder().name("docName").uuid("uuid").build();
        final Request request = Request.builder().payload(SectorMoaRequestPayload.builder()
                        .sectorMoaDocument(document)
                        .sectorAssociationId(sectorAssociationId)
                        .build())
                .build();
        addResourcesToRequest(sectorAssociationId, request);
        final List<FileInfoDTO> attachments = List.of(document);

        // invoke
        sectorMoaOfficialNoticeService.sendOfficialNotice(request);

        // assert
        verify(ccaOfficialNoticeSendService, times(1)).sendOfficialNotice(attachments, request, sectorAssociationId);
    }

    private void addResourcesToRequest(Long sectorAssociationId, Request request) {
        RequestResource sectorResource = RequestResource.builder()
                .resourceType(CcaResourceType.SECTOR_ASSOCIATION)
                .resourceId(sectorAssociationId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(sectorResource);
    }
}
