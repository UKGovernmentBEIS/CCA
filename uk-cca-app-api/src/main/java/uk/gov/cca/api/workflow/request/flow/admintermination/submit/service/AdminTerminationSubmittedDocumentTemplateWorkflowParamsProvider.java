package uk.gov.cca.api.workflow.request.flow.admintermination.submit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.CalculateAdminTerminationWithdrawExpirationRemindersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminTerminationSubmittedDocumentTemplateWorkflowParamsProvider implements
        DocumentTemplateWorkflowParamsProvider<AdminTerminationRequestPayload> {

    private final CalculateAdminTerminationWithdrawExpirationRemindersService calculateAdminTerminationWithdrawExpirationRemindersService;

    @Override
    public String getContextActionType() {
        return CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED;
    }

    @Override
    public Map<String, Object> constructParams(AdminTerminationRequestPayload payload) {
        // Get termination date, in pdf should expose the day of expiration not the day after
        LocalDate terminationDate = calculateAdminTerminationWithdrawExpirationRemindersService.getExpirationDate()
                .minusDays(1);

        return Map.of(
                "reasonDetails", payload.getAdminTerminationReasonDetails(),
                "version", "v" + payload.getUnderlyingAgreementVersion(),
                "terminationDate", terminationDate
        );
    }
}
