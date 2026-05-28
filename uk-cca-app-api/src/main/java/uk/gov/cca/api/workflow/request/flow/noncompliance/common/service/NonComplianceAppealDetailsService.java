package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceProvideAppealDetailsRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestTaskAppealable;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.transform.NonComplianceAppealDetailsMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class NonComplianceAppealDetailsService {

    private final RequestService requestService;
    private static final NonComplianceAppealDetailsMapper NON_COMPLIANCE_APPEAL_DETAILS_MAPPER = Mappers.getMapper(NonComplianceAppealDetailsMapper.class);

    @Transactional
    public void applyAppealAction(final NonComplianceProvideAppealDetailsRequestTaskActionPayload payload, RequestTask requestTask) {
        NonComplianceRequestTaskAppealable requestTaskPayload = (NonComplianceRequestTaskAppealable) requestTask.getPayload();
        requestTaskPayload.setAppealDetails(payload.getAppealDetails());
    }

    @Transactional
    public void submitAppealAction(RequestTask requestTask) {
        NonComplianceRequestTaskAppealable requestTaskPayload = (NonComplianceRequestTaskAppealable) requestTask.getPayload();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setAppealDetails(requestTaskPayload.getAppealDetails());
        requestPayload.setNonComplianceAttachments(requestTaskPayload.getNonComplianceAttachments());
    }

    public void addAppealSubmittedAction(Request request) {
        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        requestService.addActionToRequest(request,
                NON_COMPLIANCE_APPEAL_DETAILS_MAPPER.toNonComplianceAppealDetailsSubmittedRequestActionPayload(requestPayload),
                CcaRequestActionType.NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED,
                request.getPayload().getRegulatorAssignee());
    }
}
