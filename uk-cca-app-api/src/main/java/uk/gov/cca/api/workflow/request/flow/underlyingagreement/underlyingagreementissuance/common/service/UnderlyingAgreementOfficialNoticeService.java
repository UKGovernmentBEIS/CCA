package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementOfficialNoticeService {

	private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        final FileInfoDTO officialNotice = ccaFileDocumentGeneratorService.generate(
                request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_REJECTED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_REJECTED,
                "Underlying agreement rejection notice.pdf"
        );

        requestPayload.setOfficialNotice(officialNotice);
    }
    
    public CompletableFuture<FileInfoDTO> generateAcceptedOfficialNotice(final String requestId) {
    	final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_ACCEPTED,
                "Proposed underlying agreement cover letter.pdf");
    }
    
    public CompletableFuture<FileInfoDTO> generateActivatedOfficialNotice(final String requestId) {
    	final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_ACTIVATED,
                "Activated underlying agreement cover letter.pdf");
	}

    public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final List<FileInfoDTO> attachments =
                requestPayload.getUnderlyingAgreementDocument() != null ?
                    List.of(requestPayload.getOfficialNotice(), requestPayload.getUnderlyingAgreementDocument()) :
                    List.of(requestPayload.getOfficialNotice());

        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request,
        		ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification));
    }            
}
