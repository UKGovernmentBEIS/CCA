package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.UnderlyingAgreementOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementAcceptedRequestActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewDeterminationSubmittedServiceTest {

	@InjectMocks
    private UnderlyingAgreementReviewDeterminationSubmittedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementAcceptedGenerateDocumentsService generateDocumentsService;

    @Mock
    private UnderlyingAgreementOfficialNoticeService officialNoticeService;
    
    @Mock
    private CcaRequestActionUserInfoResolver resolver;
    
    @Mock
    private CcaOfficialNoticeSendService officialNoticeSendService;
    
    @Test
    void submit() {
        final String requestId = "requestId";
        final String regulator = "regulator";
        final UnderlyingAgreementPayload underlyingAgreement = UnderlyingAgreementPayload.builder()
        		.underlyingAgreement(UnderlyingAgreement.builder().build())
        		.build();
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "attachment");
        final CcaDecisionNotification ccaDecisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(UnderlyingAgreementRequestPayload.builder()
                        .regulatorReviewer(regulator)
                        .underlyingAgreement(underlyingAgreement)
                        .underlyingAgreementAttachments(attachments)
                        .decisionNotification(ccaDecisionNotification)
                        .build())
                .build();
        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "sector", RequestActionUserInfo.builder().name("Sector").roleCode("sector_user_administrator").build()
        );
        final List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );

        final UnderlyingAgreementAcceptedRequestActionPayload actionPayload =
        		UnderlyingAgreementAcceptedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_ACCEPTED_PAYLOAD)
                        .underlyingAgreement(underlyingAgreement)
                        .decisionNotification(ccaDecisionNotification)
                        .underlyingAgreementAttachments(attachments)
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(resolver.getUsersInfo(ccaDecisionNotification, request)).thenReturn(usersInfo);
        when(officialNoticeSendService.getOfficialNoticeToDefaultRecipients(request)).thenReturn(defaultContacts);

        // Invoke
        service.acceptUnderlyingAgreement(requestId);

        // Verify
        verify(requestService, times(1))
                .findRequestById(requestId);
        verify(resolver, times(1))
                .getUsersInfo(ccaDecisionNotification, request);
        verify(officialNoticeSendService, times(1))
                .getOfficialNoticeToDefaultRecipients(request);
        verify(generateDocumentsService, times(1))
        		.generateDocuments(requestId);
        verify(officialNoticeService, times(1))
                .sendOfficialNotice(requestId);
        verify(requestService, times(1))
                .addActionToRequest(request, actionPayload, CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED, regulator);
    }
}
