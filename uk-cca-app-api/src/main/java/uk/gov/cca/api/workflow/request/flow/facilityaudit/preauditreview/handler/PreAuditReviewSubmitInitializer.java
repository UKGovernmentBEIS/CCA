package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;


import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Map;
import java.util.Set;

@Service
public class PreAuditReviewSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        //TODO: preload data
        return PreAuditReviewSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.PRE_AUDIT_REVIEW_SUBMIT_PAYLOAD)
                .sectionsCompleted(Map.of())
                .build();
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.PRE_AUDIT_REVIEW_SUBMIT);
    }
}
