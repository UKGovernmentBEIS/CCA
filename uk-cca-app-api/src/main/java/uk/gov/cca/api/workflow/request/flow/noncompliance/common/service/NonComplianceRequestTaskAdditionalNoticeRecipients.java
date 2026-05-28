package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.user.operator.service.OperatorUserAuthorityInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestTaskAdditionalNoticeRecipients;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NonComplianceRequestTaskAdditionalNoticeRecipients implements RequestTaskAdditionalNoticeRecipients {

    private final OperatorUserAuthorityInfoService operatorUserAuthorityInfoService;

    @Override
    public List<AdditionalNoticeRecipientDTO> getRecipients(RequestTask requestTask, AppUser currentUser) {
        final Long accountId = requestTask.getRequest().getAccountId();

        List<AdditionalNoticeRecipientDTO> additionalNoticeRecipients = new ArrayList<>(
                operatorUserAuthorityInfoService.getCandidateOperatorNoticeRecipients(currentUser, accountId));

        return additionalNoticeRecipients.stream()
                .sorted(Comparator.comparing(NoticeRecipientDTO::getFirstName)).toList();
    }

    @Override
    public Set<String> getTypes() {
        return Set.of(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT,
                CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT,
                CcaRequestTaskType.NON_COMPLIANCE_CONCLUSION_SUBMIT);
    }
}
