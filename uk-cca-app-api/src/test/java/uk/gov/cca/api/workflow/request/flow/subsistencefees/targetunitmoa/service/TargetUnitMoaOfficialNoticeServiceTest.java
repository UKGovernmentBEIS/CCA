package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TargetUnitMoaOfficialNoticeServiceTest {

    @InjectMocks
    private TargetUnitMoaOfficialNoticeService targetUnitMoaOfficialNoticeService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void sendOfficialNotice() {
        final Long accountId = 1L;
        final FileInfoDTO document = FileInfoDTO.builder().name("docName").uuid("uuid").build();
        final Request request = Request.builder().payload(TargetUnitMoaRequestPayload.builder()
                        .targetUnitMoaDocument(document)
                        .build())
                .build();
        addResourcesToRequest(accountId, request);
        final List<FileInfoDTO> attachments = List.of(document);

        // invoke
        targetUnitMoaOfficialNoticeService.sendOfficialNotice(request);

        // assert
        verify(ccaOfficialNoticeSendService, times(1))
                .sendOfficialNotice(attachments, request, new ArrayList<>());
    }

    private void addResourcesToRequest(Long accountId, Request request) {
        RequestResource sectorResource = RequestResource.builder()
                .resourceType(ResourceType.ACCOUNT)
                .resourceId(accountId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(sectorResource);
    }
}
