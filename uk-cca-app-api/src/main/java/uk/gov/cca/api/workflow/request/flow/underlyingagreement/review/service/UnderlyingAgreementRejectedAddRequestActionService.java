package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.transform.UnderlyingAgreementReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_REJECTED;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementRejectedAddRequestActionService {

    private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private static final UnderlyingAgreementReviewMapper UNA_REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementReviewMapper.class);

    public void addRequestAction(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();

        // Get users' information
        final CcaDecisionNotification ccaDecisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = ccaRequestActionUserInfoResolver
                .getUsersInfo(ccaDecisionNotification, request);

        // Get Default notice contacts
        final List<DefaultNoticeRecipient> defaultContacts = ccaOfficialNoticeSendService
                .getOfficialNoticeToDefaultRecipients(request);

        // Create request action
        UnderlyingAgreementRejectedRequestActionPayload actionPayload = UNA_REVIEW_MAPPER
                .toUnderlyingAgreementRejectedRequestActionPayload(requestPayload, usersInfo, defaultContacts);

        requestService.addActionToRequest(request,
                actionPayload,
                UNDERLYING_AGREEMENT_APPLICATION_REJECTED,
                requestPayload.getRegulatorReviewer());
    }
}
