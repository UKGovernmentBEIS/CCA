package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNotice;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.domain.NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@AllArgsConstructor
public class NonComplianceEnforcementResponseNoticeSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        NonComplianceEnforcementResponseNotice enforcementResponseNotice = requestPayload.getEnforcementResponseNotice();

        // in case of first initialization or when penalty reissue is needed
        if (enforcementResponseNotice == null) {
            return NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                    .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                    .penaltyReissue(requestPayload.isPenaltyReissueNeeded())
                    .build();
        }
        // in case of returning from PEER_REVIEW task
        else {
            return NonComplianceEnforcementResponseNoticeSubmitRequestTaskPayload.builder()
                    .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PAYLOAD)
                    .enforcementResponseNotice(enforcementResponseNotice)
                    .penaltyReissue(requestPayload.isPenaltyReissueNeeded())
                    .sectionsCompleted(requestPayload.getSectionsCompleted())
                    .nonComplianceAttachments(requestPayload.getNonComplianceAttachments())
                    .build();
        }
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT);
    }
}
