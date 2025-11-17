package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementCreateDocumentService {

	private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;

    public CompletableFuture<FileInfoDTO> create(final String requestId, SchemeVersion version) {
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId);
        final boolean isFinalDocument = requestPayload.getUnderlyingAgreementActivationDetails() != null;
        return ccaFileDocumentGeneratorService.generateAsync(request,
        		decisionNotification,
        		getContextActionType(isFinalDocument, version),
        		getDocumentTemplateType(version),
                constructFileName(accountDetails.getBusinessId(), isFinalDocument, version ), version);
    }

	private String getDocumentTemplateType(SchemeVersion version) {
		return SchemeVersion.CCA_2.equals(version) 
				? CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA2 
						: CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA3;
	}

	private String getContextActionType(final boolean isFinalDocument, SchemeVersion version) {
		if (isFinalDocument) {
			return SchemeVersion.CCA_2.equals(version)
					? CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED_FINAL_DOCUMENT_CCA2
							: CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED_FINAL_DOCUMENT_CCA3;
		} else { 
			return SchemeVersion.CCA_2.equals(version) 
					? CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED_PROPOSED_DOCUMENT_CCA2 
							: CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED_PROPOSED_DOCUMENT_CCA3;
		}
	}
    
    private String constructFileName(final String businessId, boolean isFinalDocument, SchemeVersion version) {
        return businessId + " " +
        		version.getDescription() +
        		" Underlying Agreement v1" + 
        		(isFinalDocument ? "" : " [proposed]") +
        		".pdf";
    }
}
