package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedDocumentTemplateWorkflowParamsProvider implements
		DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;
	
	@Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACTIVATED;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementRequestPayload payload) {
        final int version = 1;

        UnderlyingAgreementTargetUnitDetails targetUnitDetails = payload.getUnderlyingAgreementProposed()
                .getUnderlyingAgreementTargetUnitDetails();

        return documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(targetUnitDetails, version);
    }
}
