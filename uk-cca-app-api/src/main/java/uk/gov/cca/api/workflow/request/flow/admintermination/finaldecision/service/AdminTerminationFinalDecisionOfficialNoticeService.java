package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.AdminTerminationOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Service
public class AdminTerminationFinalDecisionOfficialNoticeService extends AdminTerminationOfficialNoticeService {

    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    public AdminTerminationFinalDecisionOfficialNoticeService(CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService, CcaOfficialNoticeSendService ccaOfficialNoticeSendService, CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService) {
        super(ccaDecisionNotificationUsersService, ccaOfficialNoticeSendService);
        this.ccaFileDocumentGeneratorService = ccaFileDocumentGeneratorService;
    }

    @Override
    @Transactional
    public FileInfoDTO generateOfficialNotice(final Request request) {
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
}
