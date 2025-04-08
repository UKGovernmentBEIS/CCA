package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@AllArgsConstructor
public class SectorMoaOfficialNoticeService {

    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    public void sendOfficialNotice(Request request) {
        final SectorMoaRequestPayload requestPayload = (SectorMoaRequestPayload) request.getPayload();
        final List<FileInfoDTO> attachments = List.of(requestPayload.getSectorMoaDocument());
        final Long sectorAssociationId = requestPayload.getSectorAssociationId();
        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request, sectorAssociationId);
    }

}
