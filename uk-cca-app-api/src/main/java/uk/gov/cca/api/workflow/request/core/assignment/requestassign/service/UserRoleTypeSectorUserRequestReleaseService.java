package uk.gov.cca.api.workflow.request.core.assignment.requestassign.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.netz.api.workflow.request.core.assignment.requestassign.release.UserRoleTypeRequestReleaseService;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
public class UserRoleTypeSectorUserRequestReleaseService implements UserRoleTypeRequestReleaseService {

    @Override
    public String getRoleType() {
        return SECTOR_USER;
    }

    @Override
    public void release(Request request, String userId) {
        final CcaRequestPayload requestPayload = (CcaRequestPayload) request.getPayload();
        final String assignee = requestPayload.getSectorUserAssignee();
        if (!StringUtils.isEmpty(assignee)
                && (assignee.equals(userId) || StringUtils.isEmpty(userId))) {
            requestPayload.setSectorUserAssignee(null);
        }
    }
}
