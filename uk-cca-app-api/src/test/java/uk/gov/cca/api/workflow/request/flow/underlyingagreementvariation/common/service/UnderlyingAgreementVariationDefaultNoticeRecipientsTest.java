package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationDefaultNoticeRecipientsTest {

    @InjectMocks
    private UnderlyingAgreementVariationDefaultNoticeRecipients service;

    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;

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
                .accountId(accountId)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .build())
                        .build())
                .build();

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
                .isEqualTo(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION);
    }
}
