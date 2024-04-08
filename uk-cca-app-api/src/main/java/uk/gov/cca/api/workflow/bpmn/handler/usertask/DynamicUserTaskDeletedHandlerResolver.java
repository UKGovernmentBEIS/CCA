package uk.gov.cca.api.workflow.bpmn.handler.usertask;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey;
import uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDeletedHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DynamicUserTaskDeletedHandlerResolver {

    private final List<DynamicUserTaskDeletedHandler> handlers;

    public Optional<DynamicUserTaskDeletedHandler> get(final String taskDefinitionId) {

        final Optional<DynamicUserTaskDefinitionKey> taskDefinition = Arrays.stream(DynamicUserTaskDefinitionKey.values())
            .filter(v -> v.name().equals(taskDefinitionId))
            .findFirst();

        return taskDefinition.flatMap(td ->
            handlers.stream()
                .filter(handler -> td.equals(handler.getTaskDefinition()))
                .findFirst());
    }
}

