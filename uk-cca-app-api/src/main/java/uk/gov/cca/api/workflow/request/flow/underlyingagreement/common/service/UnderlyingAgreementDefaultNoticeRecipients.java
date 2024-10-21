package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementDefaultNoticeRecipients implements RequestDefaultNoticeRecipients {

    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Override
    public List<DefaultNoticeRecipient> getRecipients(Request request) {
        final Long accountId = request.getAccountId();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails =
                ((UnderlyingAgreementRequestPayload) request.getPayload())
                        .getUnderlyingAgreement()
                        .getUnderlyingAgreementTargetUnitDetails();

        return targetUnitAccountNoticeRecipients.getDefaultNoticeRecipients(accountId, targetUnitDetails);
    }

    @Override
    public String getType() {
        return CcaRequestType.UNDERLYING_AGREEMENT;
    }
}
