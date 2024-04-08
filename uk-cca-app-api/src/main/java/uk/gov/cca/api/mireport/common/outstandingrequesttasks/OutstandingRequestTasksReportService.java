package uk.gov.cca.api.mireport.common.outstandingrequesttasks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.application.taskview.RequestTaskViewService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutstandingRequestTasksReportService {

    private final RequestTaskViewService requestTaskViewService;

    @Transactional(readOnly = true)
    public Set<RequestTaskType> getRequestTaskTypesByRoleType(RoleType roleType) {
        return requestTaskViewService.getRequestTaskTypes(roleType).stream()
            .filter(requestTaskType -> !RequestTaskTypeFilter.containsExcludedRequestTaskType(requestTaskType))
            .collect(Collectors.toSet());
    }
}
