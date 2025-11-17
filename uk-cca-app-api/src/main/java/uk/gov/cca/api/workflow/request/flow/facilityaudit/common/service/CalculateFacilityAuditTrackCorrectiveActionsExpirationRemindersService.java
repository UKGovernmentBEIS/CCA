package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalculateFacilityAuditTrackCorrectiveActionsExpirationRemindersService {

    private final RequestService requestService;

    public Optional<LocalDate> calculateExpirationDate(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final FacilityAuditRequestPayload requestPayload = (FacilityAuditRequestPayload) request.getPayload();

        final Map<String, CorrectiveAction> correctiveActions = requestPayload.getAuditDetailsAndCorrectiveActions().getCorrectiveActions().getActions().stream()
                .collect(Collectors.toMap(CorrectiveAction::getTitle, Function.identity()));

        return request.getRequestTasks().stream()
                .filter(task -> CcaRequestTaskType.AUDIT_TRACK_CORRECTIVE_ACTIONS.equals(task.getType().getCode()))
                .findFirst()
                .map(requestTask -> {
                    Set<String> respondedActions =
                            ((AuditTrackCorrectiveActionsRequestTaskPayload) requestTask.getPayload()).getRespondedActions();
                    return calculateExpirationDate(correctiveActions, respondedActions);
                })
                .orElseGet(() -> calculateExpirationDate(correctiveActions, Collections.emptySet()));
    }

    private Optional<LocalDate> calculateExpirationDate(Map<String, CorrectiveAction> correctiveActions, Set<String> respondedActions) {
        return correctiveActions.entrySet()
                .stream()
                .filter(action -> !respondedActions.contains(action.getKey()))
                .map(action -> action.getValue().getDeadline())
                .min(LocalDate::compareTo);
    }
}
