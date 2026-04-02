package uk.gov.cca.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceDefaultNoticeRecipients;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceDefaultNoticeRecipientsTest {

    @InjectMocks
    private NonComplianceDefaultNoticeRecipients nonComplianceDefaultNoticeRecipients;

    @Mock
    private TargetUnitAccountNoticeRecipients noticeRecipients;

    @Test
    void getRecipients() {
        final long accountId = 1L;
        final Request request = Request.builder()
                .payload(NonComplianceRequestPayload.builder()
                        .build())
                .build();
        addResourcesToRequest(accountId, request);

        final List<DefaultNoticeRecipient> defaultNoticeRecipients = List.of(
                DefaultNoticeRecipient.builder()
                        .name("AdmFn AdmLn")
                        .email("AdmEmail")
                        .recipientType(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("ResFn ResLn")
                        .email("ResEmail")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );

        when(noticeRecipients.getDefaultAccountNoticeRecipients(accountId)).thenReturn(defaultNoticeRecipients);

        // Invoke
        nonComplianceDefaultNoticeRecipients.getRecipients(request);

        // Verify
        verify(noticeRecipients, times(1))
                .getDefaultAccountNoticeRecipients(accountId);
    }

    @Test
    void getType() {
        assertThat(nonComplianceDefaultNoticeRecipients.getType()).isEqualTo(CcaRequestType.NON_COMPLIANCE);
    }

    private void addResourcesToRequest(Long accountId, Request request) {
        RequestResource accountResource = RequestResource.builder()
                .resourceType(ResourceType.ACCOUNT)
                .resourceId(accountId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(accountResource);
    }
}
