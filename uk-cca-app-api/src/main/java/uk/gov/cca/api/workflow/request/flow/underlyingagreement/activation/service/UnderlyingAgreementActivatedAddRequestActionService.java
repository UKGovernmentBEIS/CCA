package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service;

import java.util.List;
import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.transform.UnderlyingAgreementActivationMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementActivatedAddRequestActionService {

	private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private static final UnderlyingAgreementActivationMapper UNA_ACTIVATION_MAPPER = Mappers.getMapper(UnderlyingAgreementActivationMapper.class);

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
        final UnderlyingAgreementActivatedRequestActionPayload actionPayload =
        		UNA_ACTIVATION_MAPPER.toUnderlyingAgreementActivatedRequestActionPayload(
        				requestPayload, CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_ACTIVATED_PAYLOAD, 
        				usersInfo, defaultContacts);


        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_ACTIVATED,
                requestPayload.getRegulatorAssignee());
    }
}
