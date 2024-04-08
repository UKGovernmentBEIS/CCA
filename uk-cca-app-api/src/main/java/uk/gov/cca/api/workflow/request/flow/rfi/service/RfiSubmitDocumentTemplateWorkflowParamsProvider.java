package uk.gov.cca.api.workflow.request.flow.rfi.service;

import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Component
public class RfiSubmitDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<RequestPayloadRfiable> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.RFI_SUBMIT;
    }

    @Override
    public Map<String, Object> constructParams(RequestPayloadRfiable payload) {
        return Map.of(
                "deadline", Date.from(payload.getRfiData().getRfiDeadline().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                "questions", payload.getRfiData().getRfiQuestionPayload().getQuestions(),
                "isCorsia", false
                );
    }

}
