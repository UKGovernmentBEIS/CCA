package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;

public interface RequestTaskAdditionalNoticeRecipients {

    List<AdditionalNoticeRecipientDTO> getRecipients(final RequestTask requestTask, AppUser currentUser);

    Set<String> getTypes();
}
