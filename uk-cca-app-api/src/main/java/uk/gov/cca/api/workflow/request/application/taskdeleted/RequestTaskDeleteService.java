package uk.gov.cca.api.workflow.request.application.taskdeleted;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.repository.RequestTaskRepository;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;

@Service
@RequiredArgsConstructor
public class RequestTaskDeleteService {

    private final RequestTaskRepository requestTaskRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void delete(final String processTaskId) {

        final RequestTask requestTask = requestTaskRepository.findByProcessTaskId(processTaskId);
        eventPublisher.publishEvent(RequestTaskDeletedEvent.builder().requestTaskId(requestTask.getId()).build());
        requestTaskRepository.delete(requestTask);
    }

}
