package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminTerminationFinalDecisionSubmittedService {

    private final RequestService requestService;
    private final CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private final AdminTerminationFinalDecisionOfficialNoticeService adminTerminationFinalDecisionOfficialNoticeService;

    public void submit(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();

        // Get users' information
        final CcaDecisionNotification ccaDecisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = ccaRequestActionUserInfoResolver
                .getUsersInfo(ccaDecisionNotification, request);

        // Get Default notice contacts
        final List<DefaultNoticeRecipient> defaultContacts = ccaOfficialNoticeSendService
                .getOfficialNoticeToDefaultRecipients(request);

        // Generate official notice
        FileInfoDTO officialNotice = adminTerminationFinalDecisionOfficialNoticeService.generateOfficialNotice(request);
        requestPayload.setOfficialNotice(officialNotice);

        // Create request action
        final AdminTerminationFinalDecisionSubmittedRequestActionPayload actionPayload =
                AdminTerminationFinalDecisionSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.ADMIN_TERMINATION_FINAL_DECISION_SUBMITTED_PAYLOAD)
                        .adminTerminationFinalDecisionReasonDetails(requestPayload.getAdminTerminationFinalDecisionReasonDetails())
                        .decisionNotification(ccaDecisionNotification)
                        .adminTerminationFinalDecisionAttachments(requestPayload.getAdminTerminationFinalDecisionAttachments())
                        .usersInfo(usersInfo)
                        .defaultContacts(defaultContacts)
                        .officialNotice(officialNotice)
                        .build();

        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.ADMIN_TERMINATION_FINAL_DECISION_APPLICATION_SUBMITTED,
                request.getPayload().getRegulatorAssignee());

        // Send official notice
        adminTerminationFinalDecisionOfficialNoticeService.sendOfficialNotice(request, officialNotice, ccaDecisionNotification);
    }
}
