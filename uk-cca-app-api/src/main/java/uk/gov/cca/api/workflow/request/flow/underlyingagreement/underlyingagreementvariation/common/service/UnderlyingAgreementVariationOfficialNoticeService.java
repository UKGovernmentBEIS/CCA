package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
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
                "Proposed underlying agreement variation cover letter.pdf"
                );
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

        requestPayload.setOfficialNotices(List.of(officialNotice));
    }

    public CompletableFuture<FileInfoDTO> generateActivatedOfficialNotice(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_ACTIVATED,
                "Activated underlying agreement cover letter.pdf"
        );
    }

    public CompletableFuture<FileInfoDTO> generateTerminationOfficialNotice(final String requestId, SchemeVersion schemeVersion) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final String actionType = schemeVersion.equals(SchemeVersion.CCA_2)
                ? CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION_CCA2
                : CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION_CCA3;

        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                actionType,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION,
                String.format("%s Underlying agreement variation termination notice.pdf", schemeVersion.getDescription())
        );
    }

    public void sendOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        final List<FileInfoDTO> attachments = new ArrayList<>(requestPayload.getOfficialNotices());
        if (!ObjectUtils.isEmpty(requestPayload.getUnderlyingAgreementDocuments())) {
            attachments.addAll(requestPayload.getUnderlyingAgreementDocuments().values());
        }

        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request,
                ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification));
    }
}
