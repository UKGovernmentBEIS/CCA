package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;

public interface RequestTaskDefaultNoticeRecipients {

    List<NoticeRecipientDTO> getRecipients(final RequestTask requestTask);

    Set<String> getTypes();
}
