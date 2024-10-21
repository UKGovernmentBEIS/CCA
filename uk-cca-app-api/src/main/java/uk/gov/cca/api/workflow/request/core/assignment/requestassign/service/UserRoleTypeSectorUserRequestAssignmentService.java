package uk.gov.cca.api.workflow.request.core.assignment.requestassign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.netz.api.workflow.request.core.assignment.requestassign.assign.UserRoleTypeRequestAssignmentService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@RequiredArgsConstructor
public class UserRoleTypeSectorUserRequestAssignmentService implements UserRoleTypeRequestAssignmentService {

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }

    @Override
    public void assign(Request request, String userId) {
        final CcaRequestPayload requestPayload = (CcaRequestPayload) request.getPayload();
        if (!userId.equals(requestPayload.getSectorUserAssignee())) {
            requestPayload.setSectorUserAssignee(userId);
        }
    }

}
