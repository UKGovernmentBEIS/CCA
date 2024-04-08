package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum NotificationTemplateWorkflowTaskType {

    RFI("Request for Information"),
    RDE("Determination extension request"),
    
    PAYMENT("Payment");
    
    private final String description;
    
    public static NotificationTemplateWorkflowTaskType fromRequestType(String requestType) {
        return Stream.of(values())
                .filter(workflowTaskType -> workflowTaskType.name().equalsIgnoreCase(requestType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Request type %s cannot be mapped to notification template workflow task type: ",
                                requestType)));
    }
}
