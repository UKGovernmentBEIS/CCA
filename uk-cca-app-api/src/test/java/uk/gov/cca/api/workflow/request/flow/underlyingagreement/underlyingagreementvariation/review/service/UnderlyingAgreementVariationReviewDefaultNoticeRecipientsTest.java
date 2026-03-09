package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.UnderlyingAgreementTargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewDefaultNoticeRecipientsTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewDefaultNoticeRecipients service;

    @Mock
    private UnderlyingAgreementTargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

    @Test
    void getRecipients() {
        final long accountId = 1L;
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails =
                UnderlyingAgreementTargetUnitDetails.builder()
                        .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder()
                                .firstName("ResFn")
                                .lastName("ResLn")
                                .email("ResEmail")
                                .build())
                        .build();
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
                        .build())
                .build();

        final List<NoticeRecipientDTO> defaultNoticeRecipients = List.of(
                NoticeRecipientDTO.builder()
                        .firstName("AdmFn")
                        .lastName("AdmLn")
                        .email("AdmEmail")
                        .type(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .build(),
                NoticeRecipientDTO.builder()
                        .firstName("SecFn")
                        .lastName("SecLn")
                        .email("SecEmail")
                        .type(NoticeRecipientType.SECTOR_CONTACT)
                        .build(),
                NoticeRecipientDTO.builder()
                        .firstName("ResFn")
                        .lastName("ResLn")
                        .email("ResEmail")
                        .type(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );

        when(targetUnitAccountNoticeRecipients.getNoticeRecipients(accountId, targetUnitDetails))
                .thenReturn(defaultNoticeRecipients);

        // Invoke
        service.getRecipients(requestTask);

        // Verify
        verify(targetUnitAccountNoticeRecipients, times(1))
                .getNoticeRecipients(accountId, targetUnitDetails);
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes())
                .containsExactly(CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW);
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
