package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementAcceptedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<UnderlyingAgreementRequestPayload> {

    private final DocumentTemplateUnderlyingAgreementParamsProvider documentTemplateUnderlyingAgreementParamsProvider;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_ACCEPTED;
    }

    @Override
    public Map<String, Object> constructParams(UnderlyingAgreementRequestPayload payload) {
        final int version = 1;
        UnderlyingAgreementPayload proposedUnderlyingAgreement = payload.getUnderlyingAgreementProposed();

        // Add target unit details from workflow data
        Map<String, Object> params = documentTemplateUnderlyingAgreementParamsProvider
                .constructTargetUnitDetailsTemplateParams(proposedUnderlyingAgreement.getUnderlyingAgreementTargetUnitDetails(), version);

        params.put(
                "rejectedFacilities", payload.getFacilitiesReviewGroupDecisions().entrySet()
                        .stream()
                        .filter(entry -> CcaReviewDecisionType.REJECTED.equals(entry.getValue().getType()))
                        .map(Map.Entry::getKey)
                        .toList()
        );

        return params;
    }
}
