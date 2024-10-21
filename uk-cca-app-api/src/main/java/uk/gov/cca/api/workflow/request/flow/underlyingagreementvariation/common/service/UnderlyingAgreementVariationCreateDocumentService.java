package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCreateDocumentService {

	private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;

    public CompletableFuture<FileInfoDTO> create(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId);
        return ccaFileDocumentGeneratorService.generateAsync(request,
        		decisionNotification,
        		CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT,
                constructFileName(accountDetails.getBusinessId(), requestPayload.getUnderlyingAgreementVersion() + 1));
    }
    
    private String constructFileName(final String businessId, int version) {
        return businessId + 
        		" Underlying Agreement" + 
        		" v" + 
        		version +
        		" [proposed]" +
        		".pdf";
    }
}
