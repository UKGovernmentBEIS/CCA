package uk.gov.cca.api.workflow.request.application.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestTaskDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewDefaultNoticeRecipients;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestTaskRecipientsServiceTest {

    @InjectMocks
    private RequestTaskRecipientsService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Spy
    private ArrayList<RequestTaskDefaultNoticeRecipients> requestTaskDefaultNoticeRecipients;

    @Mock
    private UnderlyingAgreementReviewDefaultNoticeRecipients underlyingAgreementReviewDefaultNoticeRecipients;

    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @BeforeEach
    void setUp() {
        requestTaskDefaultNoticeRecipients.add(underlyingAgreementReviewDefaultNoticeRecipients);
    }

    @Test
    void getDefaultNoticeRecipients() {
        final long taskId = 1L;
        final Long accountId = 11L;
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .id(taskId)
                .type(RequestTaskType.builder()
                        .code(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW)
                        .build())
                .request(request)
                .build();

        final List<NoticeRecipientDTO> expected = new ArrayList<>();
        final NoticeRecipientDTO responsiblePerson = NoticeRecipientDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.RESPONSIBLE_PERSON)
                .build();
        final NoticeRecipientDTO administrativeContact = NoticeRecipientDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                .build();
        final NoticeRecipientDTO sectorContact = NoticeRecipientDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.SECTOR_CONTACT)
                .build();
        expected.add(responsiblePerson);
        expected.add(administrativeContact);
        expected.add(sectorContact);

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(underlyingAgreementReviewDefaultNoticeRecipients.getType())
                .thenReturn(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW);
        when(underlyingAgreementReviewDefaultNoticeRecipients.getRecipients(requestTask)).thenReturn(expected);

        // Invoke
        List<NoticeRecipientDTO> actual = service.getDefaultNoticeRecipients(taskId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(requestTaskService, times(1))
                .findTaskById(taskId);
        verify(underlyingAgreementReviewDefaultNoticeRecipients, times(1))
                .getType();
        verify(underlyingAgreementReviewDefaultNoticeRecipients, times(1))
                .getRecipients(requestTask);
        verifyNoInteractions(targetUnitAccountNoticeRecipients);
    }

    @Test
    void getDefaultNoticeRecipients_contacts_from_target_unit_account() {
        final long taskId = 1L;
        final Long accountId = 11L;
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .id(taskId)
                .type(RequestTaskType.builder()
                        .code(CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_FINAL_DECISION)
                        .build())
                .request(request)
                .build();

        final List<NoticeRecipientDTO> list = List.of(
                NoticeRecipientDTO.builder()
                        .firstName("fn")
                        .lastName("ln")
                        .email("email")
                        .type(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build(),
                NoticeRecipientDTO.builder()
                        .firstName("fn")
                        .lastName("ln")
                        .email("email")
                        .type(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .build(),
                NoticeRecipientDTO.builder()
                        .firstName("fn")
                        .lastName("ln")
                        .email("email")
                        .type(NoticeRecipientType.SECTOR_CONTACT)
                        .build()
        );

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(underlyingAgreementReviewDefaultNoticeRecipients.getType())
                .thenReturn(CcaRequestTaskType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW);
        when(targetUnitAccountNoticeRecipients.getNoticeRecipients(accountId))
                .thenReturn(list);

        // Invoke
        List<NoticeRecipientDTO> defaultNoticeRecipients = service.getDefaultNoticeRecipients(taskId);

        // Verify
        assertThat(defaultNoticeRecipients).isEqualTo(list);
        verify(requestTaskService, times(1))
                .findTaskById(taskId);
        verify(underlyingAgreementReviewDefaultNoticeRecipients, times(1))
                .getType();
        verify(underlyingAgreementReviewDefaultNoticeRecipients, never())
                .getRecipients(any());
        verify(targetUnitAccountNoticeRecipients, times(1))
                .getNoticeRecipients(accountId);
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
