package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationOfficialNoticeService {

	private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    
	public CompletableFuture<FileInfoDTO> generateAcceptedOfficialNotice(final String requestId) {
    	final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED,
                "Proposed underlying agreement variation cover letter.pdf");
    }

    @Transactional
    public void generateAndSaveRejectedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        final FileInfoDTO officialNotice = ccaFileDocumentGeneratorService.generate(
                request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_REJECTED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_REJECTED,
                "Underlying agreement variation rejection notice.pdf"
        );

        requestPayload.setOfficialNotice(officialNotice);
    }
	
	public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final List<FileInfoDTO> attachments =
                requestPayload.getUnderlyingAgreementDocument() != null ?
                    List.of(requestPayload.getOfficialNotice(), requestPayload.getUnderlyingAgreementDocument()) :
                    List.of(requestPayload.getOfficialNotice());

        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request,
        		ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification));
    }

    public CompletableFuture<FileInfoDTO> generateActivatedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_ACTIVATED,
                "Activated underlying agreement cover letter.pdf");
    }
}
