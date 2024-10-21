package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum CcaNotificationTemplateWorkflowTaskType {

    ADMIN_TERMINATION("Withdraw Admin Termination");

    private final String description;

    public static CcaNotificationTemplateWorkflowTaskType fromRequestType(String requestType) {
        return Stream.of(values())
                .filter(workflowTaskType -> workflowTaskType.name().equalsIgnoreCase(requestType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Request type %s cannot be mapped to notification template workflow task type: ", requestType)));
    }
}
