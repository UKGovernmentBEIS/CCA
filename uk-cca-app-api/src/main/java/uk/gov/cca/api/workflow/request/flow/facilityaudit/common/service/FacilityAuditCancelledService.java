package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class FacilityAuditCancelledService {

    private final RequestService requestService;

    public void cancel(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final String assignee = request.getPayload().getRegulatorAssignee();

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.FACILITY_AUDIT_CANCELLED,
                assignee);
    }
}
