package uk.gov.cca.api.workflow.request.flow.admintermination.submit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.AdminTerminationOfficialNoticeService;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonCategory;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Service
public class AdminTerminationSubmitOfficialNoticeService extends AdminTerminationOfficialNoticeService {

    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    public AdminTerminationSubmitOfficialNoticeService(CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService, CcaOfficialNoticeSendService ccaOfficialNoticeSendService, CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService) {
        super(ccaDecisionNotificationUsersService, ccaOfficialNoticeSendService);
        this.ccaFileDocumentGeneratorService = ccaFileDocumentGeneratorService;
    }

    @Override
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
}
