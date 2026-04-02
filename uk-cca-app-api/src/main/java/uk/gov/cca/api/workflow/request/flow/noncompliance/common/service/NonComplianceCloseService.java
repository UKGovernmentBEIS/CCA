package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceClosedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskClosable;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.transform.NonComplianceClosedMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class NonComplianceCloseService {

    private final RequestService requestService;
    private static final NonComplianceClosedMapper NON_COMPLIANCE_CLOSED_MAPPER = Mappers.getMapper(NonComplianceClosedMapper.class);

    @Transactional
    public void applyCloseAction(final NonComplianceCloseRequestTaskActionPayload payload, RequestTask requestTask) {
        final NonComplianceCloseJustification closeJustification = payload.getCloseJustification();

        final NonComplianceRequestTaskClosable
                requestTaskPayload = (NonComplianceRequestTaskClosable) requestTask.getPayload();
        requestTaskPayload.setCloseJustification(closeJustification);
    }


    @Transactional
    public void submitCloseAction(RequestTask requestTask) {
        final NonComplianceRequestTaskClosable
                requestTaskPayload = (NonComplianceRequestTaskClosable) requestTask.getPayload();

        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setCloseJustification(requestTaskPayload.getCloseJustification());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
    }

    public void close(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        final String assignee = requestPayload.getRegulatorAssignee();

        NonComplianceClosedRequestActionPayload requestActionPayload =
                NON_COMPLIANCE_CLOSED_MAPPER.toNonComplianceClosedRequestActionPayload(requestPayload);

        requestService.addActionToRequest(request,
                requestActionPayload,
                CcaRequestActionType.NON_COMPLIANCE_CLOSED,
                assignee);
    }
}
