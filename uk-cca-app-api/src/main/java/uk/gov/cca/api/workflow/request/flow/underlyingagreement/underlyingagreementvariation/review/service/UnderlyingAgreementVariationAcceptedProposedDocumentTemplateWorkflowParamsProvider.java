package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class UnderlyingAgreementVariationAcceptedProposedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED_PROPOSED_DOCUMENT;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {

        final UnderlyingAgreementVariationPayload proposedUnderlyingAgreement = payload.getUnderlyingAgreementProposed();
        final int version = payload.getUnderlyingAgreementVersion() + 1;

        // Add target unit details from workflow data
        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(proposedUnderlyingAgreement.getUnderlyingAgreementTargetUnitDetails(), version);

        params.putAll(documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(
                proposedUnderlyingAgreement.getUnderlyingAgreement(),
                null,
                version
        ));

        return params;
    }
}
