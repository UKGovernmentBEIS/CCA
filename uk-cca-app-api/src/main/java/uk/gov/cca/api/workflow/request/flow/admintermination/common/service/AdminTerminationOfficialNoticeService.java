package uk.gov.cca.api.workflow.request.flow.admintermination.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonCategory;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTerminationOfficialNoticeService {

    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Transactional
    public FileInfoDTO generateOfficialNotice(final Request request) {
        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final Boolean isRegulatory = requestPayload.getAdminTerminationReasonDetails()
                .getReason().getCategory().equals(AdminTerminationReasonCategory.REGULATORY);

        return ccaFileDocumentGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED,
                Boolean.TRUE.equals(isRegulatory) ? CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_SUBMITTED
                        : CcaDocumentTemplateType.ADMIN_TERMINATION_ADMINISTRATIVE_SUBMITTED,
                Boolean.TRUE.equals(isRegulatory) ? "Notice of intent to terminate agreement.pdf"
                        : "Termination notice.pdf");
    }

    @Transactional
    public FileInfoDTO generateFinalDecisionOfficialNotice(final Request request) {
        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final AdminTerminationFinalDecisionType decisionType = requestPayload.getAdminTerminationFinalDecisionReasonDetails()
                .getFinalDecisionType();

        return ccaFileDocumentGeneratorService.generate(request,
                decisionNotification,
                decisionType.equals(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                        ? CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_FINALISED
                        : CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINAL_DECISION_WITHDRAWN,
                decisionType.equals(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                        ? CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_TERMINATED
                        : CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_WITHDRAWN,
                decisionType.equals(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                        ? "Admin Termination Regulatory reason notice.pdf"
                        : "Withdrawal of intent to terminate agreement.pdf");
    }

    @Transactional
    public FileInfoDTO generateWithdrawOfficialNotice(final Request request) {
        final AdminTerminationRequestPayload requestPayload = (AdminTerminationRequestPayload) request.getPayload();
        final CcaDecisionNotification decisionNotification = requestPayload.getDecisionNotification();

        return ccaFileDocumentGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_WITHDRAWN,
                CcaDocumentTemplateType.ADMIN_TERMINATION_REGULATORY_WITHDRAWN,
                "Withdrawal of intent to terminate agreement.pdf");
    }

    public void sendOfficialNotice(Request request, FileInfoDTO officialNotice,
                                   CcaDecisionNotification decisionNotification) {
        ccaOfficialNoticeSendService.sendOfficialNotice(List.of(officialNotice), request,
                ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification));
    }
}
