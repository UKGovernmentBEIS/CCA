package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.util.HashMap;
import java.util.Map;

@Component
public class BuyOutSurplusDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<BuyOutSurplusAccountProcessingRequestPayload> {

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.BUY_OUT_SURPLUS_NOTICE;
    }

    @Override
    public Map<String, Object> constructParams(BuyOutSurplusAccountProcessingRequestPayload payload) {
        return new HashMap<>(Map.of(
                "buyOut", payload.getBuyOutSurplus(),
                "submissionDate", payload.getPerformanceData().getSubmissionDate()
        ));
    }
}
