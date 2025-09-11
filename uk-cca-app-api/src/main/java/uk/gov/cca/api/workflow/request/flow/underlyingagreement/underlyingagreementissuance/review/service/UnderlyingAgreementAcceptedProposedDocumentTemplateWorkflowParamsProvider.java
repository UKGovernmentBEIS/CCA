package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class UnderlyingAgreementAcceptedProposedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED_PROPOSED_DOCUMENT;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementRequestPayload payload) {
        final UnderlyingAgreementPayload proposedUnderlyingAgreement = payload.getUnderlyingAgreementProposed();
        final int version = 1;

        // Add target unit details from workflow data
        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(proposedUnderlyingAgreement.getUnderlyingAgreementTargetUnitDetails(), version);

        params.putAll(
                documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(
                        proposedUnderlyingAgreement.getUnderlyingAgreement(),
                        null,
                        version)
        );

        return params;
    }
}
