package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceNoticeOfIntentSubmitInitializerTest {

    @InjectMocks
    private NonComplianceNoticeOfIntentSubmitInitializer nonComplianceNoticeOfIntentSubmitInitializer;


    @Test
    void initializePayload() {
        final String accountId = "1";
        final Request request = Request.builder()
                .id("ADS_1-T00003-NCOM-1")
                .payload(NonComplianceRequestPayload.builder().build())
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(accountId)
                        .resourceType(ResourceType.ACCOUNT)
                        .build()))
                .build();

        NonComplianceNoticeOfIntentSubmitRequestTaskPayload expected = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .build();

        // Invoke
        RequestTaskPayload actual = nonComplianceNoticeOfIntentSubmitInitializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(NonComplianceNoticeOfIntentSubmitRequestTaskPayload.class).isEqualTo(expected);
    }


    @Test
    void getRequestTaskTypes() {
        assertThat(nonComplianceNoticeOfIntentSubmitInitializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT);
    }
}
