package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
        final int version = payload.getUnderlyingAgreementVersion() + 1;

        UnderlyingAgreementTargetUnitDetails targetUnitDetails = payload.getUnderlyingAgreementProposed()
                .getUnderlyingAgreementTargetUnitDetails();

        return documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails, version);
    }
}
