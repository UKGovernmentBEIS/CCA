package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationRejectedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.transform.UnderlyingAgreementVariationReviewMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationRejectedAddRequestActionService {

    private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private static final UnderlyingAgreementVariationReviewMapper UNA_VARIATION_REVIEW_MAPPER = Mappers.getMapper(UnderlyingAgreementVariationReviewMapper.class);

    public void addRequestAction(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();

        // Get users' information
        final CcaDecisionNotification ccaDecisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = ccaRequestActionUserInfoResolver
                .getUsersInfo(ccaDecisionNotification, request);

        // Get Default notice contacts
        final List<DefaultNoticeRecipient> defaultContacts = ccaOfficialNoticeSendService
                .getOfficialNoticeToDefaultRecipients(request);

        // Create request action
        UnderlyingAgreementVariationRejectedRequestActionPayload actionPayload = UNA_VARIATION_REVIEW_MAPPER
                .toUnderlyingAgreementVariationRejectedRequestActionPayload(requestPayload, usersInfo, defaultContacts);

        requestService.addActionToRequest(request,
                actionPayload,
                UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REJECTED,
                requestPayload.getRegulatorReviewer());
    }
}
