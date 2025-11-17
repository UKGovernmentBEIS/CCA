package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

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
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCreateDocumentService {

	private final RequestService requestService;
    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;

    public CompletableFuture<FileInfoDTO> create(final String requestId, SchemeVersion schemeVersion) {
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId);
        final boolean isFinalDocument = requestPayload.getUnderlyingAgreementActivationDetails() != null;
        return ccaFileDocumentGeneratorService.generateAsync(request,
                decisionNotification,
                getContextActionType(isFinalDocument, schemeVersion),
        		getDocumentTemplateType(schemeVersion),
                constructFileName(accountDetails.getBusinessId(), requestPayload.getUnderlyingAgreementVersionMap().getOrDefault(schemeVersion, 0) + 1, isFinalDocument, schemeVersion), 
                schemeVersion);
    }
    
    private String getDocumentTemplateType(SchemeVersion schemeVersion) {
		return SchemeVersion.CCA_2.equals(schemeVersion) 
				? CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA2 
						: CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA3;
	}

	private String getContextActionType(final boolean isFinalDocument, SchemeVersion schemeVersion) {
		if (isFinalDocument) {
			return SchemeVersion.CCA_2.equals(schemeVersion)
					? CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_FINAL_DOCUMENT_CCA2
					: CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_FINAL_DOCUMENT_CCA3;
		} else { 
			return SchemeVersion.CCA_2.equals(schemeVersion) 
					? CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT_CCA2 
							: CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT_CCA3;
		}
	}

    private String constructFileName(final String businessId, final int version, final boolean isFinalDocument, SchemeVersion schemeVersion) {
        return businessId + " " +
        		schemeVersion.getDescription() +
                " Underlying Agreement" +
                " v" +
                version +
                (isFinalDocument ? "" : " [proposed]") +
                ".pdf";
    }
}
