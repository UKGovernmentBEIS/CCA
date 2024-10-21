package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

public interface RequestDefaultNoticeRecipients {

    List<DefaultNoticeRecipient> getRecipients(final Request request);

    String getType();
}
