package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service.UnderlyingAgreementOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementAcceptedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.transform.UnderlyingAgreementReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementReviewDeterminationSubmittedService {

    private final UnderlyingAgreementOfficialNoticeService underlyingAgreementOfficialNoticeService;
    private final UnderlyingAgreementAcceptedGenerateDocumentsService underlyingAgreementAcceptedGenerateDocumentsService;
    private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private static final UnderlyingAgreementReviewMapper UNA_REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementReviewMapper.class);

    public void acceptUnderlyingAgreement(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementRequestPayload requestPayload = (UnderlyingAgreementRequestPayload) request.getPayload();

        // Get users' information
        final CcaDecisionNotification ccaDecisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = ccaRequestActionUserInfoResolver
                .getUsersInfo(ccaDecisionNotification, request);

        // Get Default notice contacts
        final List<DefaultNoticeRecipient> defaultContacts = ccaOfficialNoticeSendService
                .getOfficialNoticeToDefaultRecipients(request);

        // Generate official notice and UNA document
        underlyingAgreementAcceptedGenerateDocumentsService.generateDocuments(requestId);

        // Create request action
        final UnderlyingAgreementAcceptedRequestActionPayload actionPayload =
        		UNA_REVIEW_MAPPER.toUnderlyingAgreementAcceptedRequestActionPayload(
        				requestPayload, usersInfo, defaultContacts);

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_ACCEPTED,
                request.getPayload().getRegulatorReviewer());

        // Send determination notice
        underlyingAgreementOfficialNoticeService.sendOfficialNotice(requestId);
    }
}
