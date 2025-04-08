package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class UnderlyingAgreementVariationActivatedFinalDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementVariationRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_FINAL_DOCUMENT;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementVariationRequestPayload payload) {
        final int version = payload.getUnderlyingAgreementVersion() + 1;

        final UnderlyingAgreementVariationPayload proposedUnderlyingAgreement = payload.getUnderlyingAgreementProposed();

        // Add target unit details from workflow data
        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(proposedUnderlyingAgreement.getUnderlyingAgreementTargetUnitDetails(), version);

        params.putAll(documentTemplateUnderlyingAgreementParamsProvider.constructTemplateParams(
                proposedUnderlyingAgreement.getUnderlyingAgreement(),
                documentTemplateTransformationMapper.formatCurrentDate(),
                version));

        return params;
    }
}
