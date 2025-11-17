package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.transform.Cca2ExtensionNoticeAccountProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingService {

    private final RequestService requestService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private final Cca2ExtensionNoticeAccountProcessingGenerateDocumentsService cca2ExtensionNoticeGenerateDocumentsService;
    private final Cca2ExtensionNoticeAccountProcessingOfficialNoticeService cca2ExtensionNoticeProcessingOfficialNoticeService;
    private static final Cca2ExtensionNoticeAccountProcessingMapper MAPPER = Mappers
            .getMapper(Cca2ExtensionNoticeAccountProcessingMapper.class);

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void doProcess(String requestId, Cca2ExtensionNoticeAccountState accountState) throws BpmnExecutionException {
        try {
            Request request = requestService.findRequestById(requestId);

            // Get Default notice contacts
            final List<DefaultNoticeRecipient> defaultContacts = ccaOfficialNoticeSendService
                    .getOfficialNoticeToDefaultRecipients(request);
            Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                    (Cca2ExtensionNoticeAccountProcessingRequestPayload) request.getPayload();
            requestPayload.setDefaultContacts(defaultContacts);

            // Create documents
            cca2ExtensionNoticeGenerateDocumentsService.generateDocuments(request, accountState);

            // Create timeline
            Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload actionPayload = MAPPER
                    .toCca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload(requestPayload);

            requestService.addActionToRequest(request,
                    actionPayload,
                    CcaRequestActionType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED,
                    null);

            // Send notice
            cca2ExtensionNoticeProcessingOfficialNoticeService.sendOfficialNotice(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(e.getMessage()));
        }
    }
}
