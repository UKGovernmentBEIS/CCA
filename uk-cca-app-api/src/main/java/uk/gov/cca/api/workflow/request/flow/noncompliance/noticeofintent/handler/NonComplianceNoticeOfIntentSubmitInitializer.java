package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntent;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.InitializeRequestTaskHandler;

import java.util.Set;

@Service
@AllArgsConstructor
public class NonComplianceNoticeOfIntentSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        NonComplianceNoticeOfIntent noticeOfIntent = requestPayload.getNoticeOfIntent();
        if (noticeOfIntent != null) {
            return NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                    .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                    .noticeOfIntent(noticeOfIntent)
                    .sectionsCompleted(requestPayload.getSectionsCompleted())
                    .nonComplianceAttachments(requestPayload.getNonComplianceAttachments())
                    .build();
        } else {
            return NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                    .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                    .build();
        }
    }

    @Override
    public Set<String> getRequestTaskTypes() {
        return Set.of(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT);
    }
}
