package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NonComplianceDefaultNoticeRecipients implements RequestDefaultNoticeRecipients {

    private final TargetUnitAccountNoticeRecipients noticeRecipients;

    @Override
    public List<DefaultNoticeRecipient> getRecipients(Request request) {
        return noticeRecipients.getDefaultAccountNoticeRecipients(request.getAccountId());
    }

    @Override
    public String getType() {
        return CcaRequestType.NON_COMPLIANCE;
    }
}
