package uk.gov.cca.api.workflow.request.flow.noncompliance.details.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.service.NonComplianceDetailsSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@RequiredArgsConstructor
@Component
public class NonComplianceDetailsSubmitSaveActionHandler implements RequestTaskActionHandler<NonComplianceDetailsSubmitSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NonComplianceDetailsSubmitService nonComplianceDetailsSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, NonComplianceDetailsSubmitSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        nonComplianceDetailsSubmitService.applySaveAction(payload, requestTask);

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_DETAILS_SAVE_APPLICATION);
    }
}
