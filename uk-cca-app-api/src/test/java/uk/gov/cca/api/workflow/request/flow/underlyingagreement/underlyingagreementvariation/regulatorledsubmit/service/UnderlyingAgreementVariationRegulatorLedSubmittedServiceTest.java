package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationAcceptedGenerateDocumentsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRegulatorLedSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmittedServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmittedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Mock
    private UnderlyingAgreementVariationAcceptedGenerateDocumentsService underlyingAgreementVariationAcceptedGenerateDocumentsService;

    @Mock
    private UnderlyingAgreementVariationOfficialNoticeService underlyingAgreementVariationOfficialNoticeService;

    @Test
    void submittedUnderlyingAgreementVariation() {
        final String requestId = "requestId";
        final String assignee = "regulator";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .build();
        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .regulatorAssignee(assignee)
                .decisionNotification(decisionNotification)
                .regulatorLedDetermination(determination)
                .build();
        final Request request = Request.builder()
                .payload(payload)
                .build();

        final Map<String, RequestActionUserInfo> usersInfo = Map.of("user", RequestActionUserInfo.builder().name("User").build());
        final List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final UnderlyingAgreementVariationRegulatorLedSubmittedRequestActionPayload actionPayload =
                UnderlyingAgreementVariationRegulatorLedSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMITTED_PAYLOAD)
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .decisionNotification(decisionNotification)
                        .determination(determination)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaRequestActionUserInfoResolver.getUsersInfo(decisionNotification, request))
                .thenReturn(usersInfo);
        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request))
                .thenReturn(defaultContacts);

        // Invoke
        service.submittedUnderlyingAgreementVariation(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaRequestActionUserInfoResolver, times(1))
                .getUsersInfo(decisionNotification, request);
        verify(ccaOfficialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(underlyingAgreementVariationAcceptedGenerateDocumentsService, times(1))
                .generateDocuments(requestId);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_SUBMITTED, assignee);
        verify(underlyingAgreementVariationOfficialNoticeService, times(1)).sendOfficialNotice(requestId);
    }
}
