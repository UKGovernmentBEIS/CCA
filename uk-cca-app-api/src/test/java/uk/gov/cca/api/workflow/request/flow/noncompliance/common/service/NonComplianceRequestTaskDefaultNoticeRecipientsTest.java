package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceRequestTaskDefaultNoticeRecipientsTest {

    @InjectMocks
    private NonComplianceRequestTaskDefaultNoticeRecipients service;

    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Test
    void getRecipients() {
        final Long accountId = 1L;
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(NonComplianceRequestPayload.builder()
                                .build())
                        .requestResources(List.of(RequestResource.builder()
                                .resourceType(ResourceType.ACCOUNT)
                                .resourceId(accountId.toString())
                                .build())
                        ).build())
                .build();
        final NoticeRecipientDTO administrative = NoticeRecipientDTO.builder()
                .firstName("AdministrativeFn")
                .lastName("AdministrativeLn")
                .email("administrative@test.com")
                .type(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                .build();
        final NoticeRecipientDTO responsible = NoticeRecipientDTO.builder()
                .firstName("ResponsibleFn")
                .lastName("ResponsibleLn")
                .email("responsiblePerson@test.com")
                .type(NoticeRecipientType.RESPONSIBLE_PERSON)
                .build();
        List<NoticeRecipientDTO> recipients = Stream.of(administrative, responsible).toList();

        when(targetUnitAccountNoticeRecipients.getAccountNoticeRecipients(accountId))
                .thenReturn(recipients);

        // Invoke
        List<NoticeRecipientDTO> result = service.getRecipients(requestTask);

        // Verify
        assertThat(result).containsExactlyInAnyOrder(administrative, responsible);
        verify(targetUnitAccountNoticeRecipients, times(1)).getAccountNoticeRecipients(accountId);
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes()).containsExactlyInAnyOrder(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT,
                CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT);
    }

}
