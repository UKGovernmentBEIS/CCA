package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service;

import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;

public interface UserRoleRequestTaskDefaultAssignmentService {

    void assignDefaultAssigneeToTask(RequestTask requestTask);
    RoleType getRoleType();
}
