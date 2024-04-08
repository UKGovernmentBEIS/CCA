package uk.gov.cca.api.workflow.request.flow.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.RequestTask;

@Service
@RequiredArgsConstructor
public class SupportingTaskAssignmentValidator {

    private final RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    public void validate(RequestTask requestTask, RequestTaskType requestTaskType, String selectedAssignee, AppUser appUser) {
        if (!requestTaskAssignmentValidationService
            .hasUserPermissionsToBeAssignedToTaskType(requestTask, requestTaskType, selectedAssignee, appUser)) {
            throw new BusinessException(ErrorCode.ASSIGNMENT_NOT_ALLOWED);
        }
    }
}
