package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification.UnderlyingAgreementTargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementDefaultNoticeRecipientsTest {

    @InjectMocks
    private UnderlyingAgreementDefaultNoticeRecipients service;

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
        final Request request = Request.builder()
                .payload(UnderlyingAgreementRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
                        .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
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
                        .name("SecFn SecLn")
                        .email("SecEmail")
                        .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                        .build(),
                DefaultNoticeRecipient.builder()
                        .name("ResFn ResLn")
                        .email("ResEmail")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );

        when(targetUnitAccountNoticeRecipients.getDefaultNoticeRecipients(accountId, targetUnitDetails))
                .thenReturn(defaultNoticeRecipients);

        // Invoke
        service.getRecipients(request);

        // Verify
        verify(targetUnitAccountNoticeRecipients, times(1))
                .getDefaultNoticeRecipients(accountId, targetUnitDetails);
    }

    @Test
    void getType() {
        assertThat(service.getType())
                .isEqualTo(CcaRequestType.UNDERLYING_AGREEMENT);
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
