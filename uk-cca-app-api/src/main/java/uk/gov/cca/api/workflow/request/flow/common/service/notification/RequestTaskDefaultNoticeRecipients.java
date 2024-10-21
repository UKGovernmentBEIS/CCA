package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

public interface RequestTaskDefaultNoticeRecipients {

    List<NoticeRecipientDTO> getRecipients(final RequestTask requestTask);

    String getType();
}
