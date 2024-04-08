package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import uk.gov.cca.api.workflow.request.core.domain.Payload;

import java.util.Map;

public interface DocumentTemplateWorkflowParamsProvider<T extends Payload> {

    DocumentTemplateGenerationContextActionType getContextActionType();
    
    Map<String, Object> constructParams(T payload);
    
}
