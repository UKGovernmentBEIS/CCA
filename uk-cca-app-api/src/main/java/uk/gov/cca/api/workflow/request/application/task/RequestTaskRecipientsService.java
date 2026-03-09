package uk.gov.cca.api.workflow.request.application.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestTaskDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestTaskRecipientsService {

    private final RequestTaskService requestTaskService;
    private final List<RequestTaskDefaultNoticeRecipients> requestTaskDefaultNoticeRecipients;
    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Transactional
    public List<NoticeRecipientDTO> getDefaultNoticeRecipients(Long taskId) {
        final RequestTask requestTask = requestTaskService.findTaskById(taskId);
        final RequestTaskType taskType = requestTask.getType();
        final Long accountId = requestTask.getRequest().getAccountId();

        List<NoticeRecipientDTO> defaultNoticeRecipients = new ArrayList<>();

        // Get default recipients per task otherwise get from target unit account
        requestTaskDefaultNoticeRecipients.stream()
                .filter(service -> service.getTypes().contains(taskType.getCode()))
                .findFirst().ifPresentOrElse(
                        service -> defaultNoticeRecipients.addAll(service.getRecipients(requestTask)),
                        () -> defaultNoticeRecipients.addAll(targetUnitAccountNoticeRecipients.getNoticeRecipients(accountId))
                );

        return defaultNoticeRecipients;
    }
}
