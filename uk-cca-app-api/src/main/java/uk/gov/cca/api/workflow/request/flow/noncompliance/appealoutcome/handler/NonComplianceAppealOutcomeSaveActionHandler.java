package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.service.NonComplianceAppealOutcomeService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@AllArgsConstructor
public class NonComplianceAppealOutcomeSaveActionHandler implements RequestTaskActionHandler<NonComplianceAppealOutcomeSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NonComplianceAppealOutcomeService nonComplianceAppealOutcomeService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, NonComplianceAppealOutcomeSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        nonComplianceAppealOutcomeService.save(payload, requestTask);
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_APPEAL_OUTCOME_SAVE_APPLICATION);
    }
}