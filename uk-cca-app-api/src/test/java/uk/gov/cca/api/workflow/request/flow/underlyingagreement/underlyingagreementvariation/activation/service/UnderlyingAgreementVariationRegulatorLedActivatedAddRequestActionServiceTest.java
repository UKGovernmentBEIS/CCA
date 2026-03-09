package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

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
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
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
class UnderlyingAgreementVariationRegulatorLedActivatedAddRequestActionServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedActivatedAddRequestActionService service;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void addRequestAction() {
        final String requestId = "requestId";
        final String assignee = "regulator";
        final UnderlyingAgreementActivationDetails activationDetails = UnderlyingAgreementActivationDetails.builder()
                .comments("comments")
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final UnderlyingAgreementVariationRequestPayload payload = UnderlyingAgreementVariationRequestPayload.builder()
                .regulatorAssignee(assignee)
                .underlyingAgreementActivationDetails(activationDetails)
                .decisionNotification(decisionNotification)
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
        final UnderlyingAgreementVariationActivatedRequestActionPayload actionPayload =
                UnderlyingAgreementVariationActivatedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_PAYLOAD)
                        .underlyingAgreementActivationDetails(activationDetails)
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .decisionNotification(decisionNotification)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaRequestActionUserInfoResolver.getUsersInfo(decisionNotification, request))
                .thenReturn(usersInfo);
        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request))
                .thenReturn(defaultContacts);

        // Invoke
        service.addRequestAction(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaRequestActionUserInfoResolver, times(1))
                .getUsersInfo(decisionNotification, request);
        verify(ccaOfficialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_ACTIVATED, assignee);
    }
}
