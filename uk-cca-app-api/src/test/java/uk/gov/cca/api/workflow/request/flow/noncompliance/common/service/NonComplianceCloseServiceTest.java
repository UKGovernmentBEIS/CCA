package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseJustification;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceCloseRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceClosedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceCloseServiceTest {

    @InjectMocks
    private NonComplianceCloseService nonComplianceCloseService;

    @Mock
    private RequestService requestService;

    @Test
    void applyCloseAction() {

        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceCloseJustification closeJustification = NonComplianceCloseJustification.builder()
                .reason("reason")
                .files(Set.of(fileUuid))
                .build();
        final NonComplianceCloseRequestTaskActionPayload requestTaskActionPayload = NonComplianceCloseRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.NON_COMPLIANCE_CLOSE_TASK_PAYLOAD)
                .closeJustification(closeJustification)
                .build();
        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(NonComplianceRequestPayload.builder().build())
                        .build())
                .payload(requestTaskPayload)
                .build();

        // invoke
        nonComplianceCloseService.applyCloseAction(requestTaskActionPayload, requestTask);

        // verify
        NonComplianceNoticeOfIntentSubmitRequestTaskPayload taskPayload = (NonComplianceNoticeOfIntentSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(taskPayload.getCloseJustification()).isEqualTo(closeJustification);
    }

    @Test
    void submitCloseAction() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceCloseJustification closeJustification = NonComplianceCloseJustification.builder()
                .reason("reason")
                .files(Set.of(fileUuid))
                .build();
        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .closeJustification(closeJustification)
                .build();
        final Request request = Request.builder()
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(requestTaskPayload)
                .build();

        // invoke
        nonComplianceCloseService.submitCloseAction(requestTask);

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.getCloseJustification()).isEqualTo(closeJustification);
    }

    @Test
    void close() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceCloseJustification closeJustification = NonComplianceCloseJustification.builder()
                .reason("reason")
                .files(Set.of(fileUuid))
                .build();
        final String requestId = "requestId";
        final String regulatorAssignee = "bbb2820b-cbc6-4923-b3f1-8de409ea34c1";
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .closeJustification(closeJustification)
                .regulatorAssignee(regulatorAssignee)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final NonComplianceClosedRequestActionPayload requestActionPayload = NonComplianceClosedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_CLOSED_PAYLOAD)
                .closeJustification(closeJustification)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // invoke
        nonComplianceCloseService.close(requestId);

        // verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request,
                requestActionPayload,
                CcaRequestActionType.NON_COMPLIANCE_CLOSED,
                regulatorAssignee);
    }
}
