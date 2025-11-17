package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingServiceTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingService service;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Mock
    private Cca2ExtensionNoticeAccountProcessingGenerateDocumentsService cca2ExtensionNoticeGenerateDocumentsService;

    @Mock
    private Cca2ExtensionNoticeAccountProcessingOfficialNoticeService cca2ExtensionNoticeProcessingOfficialNoticeService;

    @Test
    void doProcess() throws BpmnExecutionException {
        final String requestId = "requestId";
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(1L)
                .build();

        final Request request = Request.builder()
                .payload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                        .officialNotice(FileInfoDTO.builder().name("notice").build())
                        .underlyingAgreementDocument(FileInfoDTO.builder().name("document").build())
                        .build())
                .build();
        final List<DefaultNoticeRecipient> defaultContacts = List.of(DefaultNoticeRecipient.builder().email("email").build());
        final Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload actionPayload =
                Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
                        .officialNotice(FileInfoDTO.builder().name("notice").build())
                        .underlyingAgreementDocument(FileInfoDTO.builder().name("document").build())
                        .defaultContacts(defaultContacts)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request)).thenReturn(defaultContacts);

        // Invoke
        service.doProcess(requestId, accountState);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaOfficialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(cca2ExtensionNoticeGenerateDocumentsService, times(1))
                .generateDocuments(request, accountState);
        verify(requestService, times(1)).addActionToRequest(
                request, actionPayload, CcaRequestActionType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED, null);
        verify(cca2ExtensionNoticeProcessingOfficialNoticeService, times(1))
                .sendOfficialNotice(request);
    }
}
