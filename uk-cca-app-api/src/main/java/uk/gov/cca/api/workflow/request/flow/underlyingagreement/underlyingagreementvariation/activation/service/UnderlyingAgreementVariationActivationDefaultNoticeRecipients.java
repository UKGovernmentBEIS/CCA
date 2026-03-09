package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestTaskDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.UnderlyingAgreementTargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivationDefaultNoticeRecipients implements RequestTaskDefaultNoticeRecipients {

    private final UnderlyingAgreementTargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Override
    public List<NoticeRecipientDTO> getRecipients(RequestTask requestTask) {
        final Long accountId = requestTask.getRequest().getAccountId();
        final UnderlyingAgreementVariationRequestPayload requestPayload =
                (UnderlyingAgreementVariationRequestPayload) requestTask.getRequest().getPayload();

        return targetUnitAccountNoticeRecipients.getNoticeRecipients(accountId,
                requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails());
    }

    @Override
    public Set<String> getTypes() {
        return Set.of(
                CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION,
                CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_ACTIVATION
        );
    }
}
