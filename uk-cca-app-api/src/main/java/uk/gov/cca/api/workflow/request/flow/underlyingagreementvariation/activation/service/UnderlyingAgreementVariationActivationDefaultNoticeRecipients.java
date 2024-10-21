package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestTaskDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivationDefaultNoticeRecipients implements RequestTaskDefaultNoticeRecipients {

    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Override
    public List<NoticeRecipientDTO> getRecipients(RequestTask requestTask) {
        final Long accountId = requestTask.getRequest().getAccountId();
        final UnderlyingAgreementVariationRequestPayload requestPayload =
                (UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload();

        return targetUnitAccountNoticeRecipients.getNoticeRecipients(accountId,
                requestPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails());
    }

    @Override
    public String getType() {
        return CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION;
    }
}
