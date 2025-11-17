package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationRejectedRequestActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REJECTED_PAYLOAD;
import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRejectedAddRequestActionServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRejectedAddRequestActionService service;

    @Mock
    private RequestService requestService;
    @Mock
    private CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void addRequestAction() {
        final String requestId ="1";

        final CcaDecisionNotification ccaDecisionNotification = CcaDecisionNotification.builder()
                .build();

        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .decisionNotification(ccaDecisionNotification)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final List<DefaultNoticeRecipient> defaultContacts = Collections.singletonList(DefaultNoticeRecipient.builder().build());

        final Map<String, RequestActionUserInfo> usersInfo = Collections.singletonMap("String",RequestActionUserInfo.builder().build());

        when(requestService.findRequestById(requestId)).thenReturn(request);

        when(ccaRequestActionUserInfoResolver.getUsersInfo(ccaDecisionNotification, request))
                .thenReturn(usersInfo);

        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request))
                .thenReturn(defaultContacts);

        UnderlyingAgreementVariationRejectedRequestActionPayload actionPayload =
                UnderlyingAgreementVariationRejectedRequestActionPayload.builder()
                        .payloadType(UNDERLYING_AGREEMENT_VARIATION_REJECTED_PAYLOAD)
                        .decisionNotification(ccaDecisionNotification)
                        .defaultContacts(defaultContacts)
                        .usersInfo(usersInfo)
                        .reviewAttachments(requestPayload.getUnderlyingAgreementAttachments())
                        .determination(requestPayload.getDetermination())
                        .build();

        // Invoke
        service.addRequestAction(requestId);

        verify(requestService, times(1))
                .addActionToRequest(request,
                        actionPayload,
                        UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED,
                        requestPayload.getRegulatorReviewer());
    }
}