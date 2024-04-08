package uk.gov.cca.api.workflow.request.flow.common.taskhandler;

public interface CustomUserTaskCreatedHandler extends UserTaskCreatedHandler {

    String getTaskDefinitionKey();

}
