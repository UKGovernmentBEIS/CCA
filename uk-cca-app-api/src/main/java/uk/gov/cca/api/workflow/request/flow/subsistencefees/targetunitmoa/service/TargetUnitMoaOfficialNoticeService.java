package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TargetUnitMoaOfficialNoticeService {

    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    public void sendOfficialNotice(Request request) {
        final TargetUnitMoaRequestPayload requestPayload = (TargetUnitMoaRequestPayload) request.getPayload();
        final List<FileInfoDTO> attachments = List.of(requestPayload.getTargetUnitMoaDocument());
        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request, new ArrayList<>());
    }
}
