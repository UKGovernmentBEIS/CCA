package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.transform.UnderlyingAgreementVariationActivationMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedAddRequestActionService {

    private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private static final UnderlyingAgreementVariationActivationMapper UNA_VARIATION_ACTIVATION_MAPPER =
            Mappers.getMapper(UnderlyingAgreementVariationActivationMapper.class);

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
        final UnderlyingAgreementVariationActivatedRequestActionPayload actionPayload =
                UNA_VARIATION_ACTIVATION_MAPPER.toUnderlyingAgreementVariationActivatedRequestActionPayload(
                        requestPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_PAYLOAD,
                        usersInfo, defaultContacts);


        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_ACTIVATED,
                requestPayload.getRegulatorAssignee());
    }
}
