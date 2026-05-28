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
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceProvideAppealDetailsRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceAppealDetailsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain.NonComplianceConclusionSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealDetailsServiceTest {

    @InjectMocks
    private NonComplianceAppealDetailsService service;

    @Mock
    private RequestService requestService;

    @Test
    void applyAppealAction() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealDetails appealDetails = NonComplianceAppealDetails.builder()
                .files(Set.of(fileUuid))
                .registrationDate(LocalDate.now().minusDays(1))
                .comments("bla bla bla")
                .build();
        final NonComplianceProvideAppealDetailsRequestTaskActionPayload requestTaskActionPayload = NonComplianceProvideAppealDetailsRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.NON_COMPLIANCE_PROVIDE_APPEAL_DETAILS_PAYLOAD)
                .appealDetails(appealDetails)
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(NonComplianceRequestPayload.builder().build())
                        .build())
                .payload(requestTaskPayload)
                .build();

        // invoke
        service.applyAppealAction(requestTaskActionPayload, requestTask);

        // verify
        NonComplianceConclusionSubmitRequestTaskPayload taskPayload = (NonComplianceConclusionSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(taskPayload.getAppealDetails()).isEqualTo(appealDetails);
    }

    @Test
    void submitAppealAction() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealDetails appealDetails = NonComplianceAppealDetails.builder()
                .files(Set.of(fileUuid))
                .registrationDate(LocalDate.now().minusDays(1))
                .comments("bla bla bla")
                .build();
        final NonComplianceConclusionSubmitRequestTaskPayload requestTaskPayload = NonComplianceConclusionSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_CONCLUSION_SUBMIT_PAYLOAD)
                .appealDetails(appealDetails)
                .build();
        final Request request = Request.builder()
                .payload(NonComplianceRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(requestTaskPayload)
                .build();

        // invoke
        service.submitAppealAction(requestTask);

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.getAppealDetails()).isEqualTo(appealDetails);
    }

    @Test
    void addAppealSubmittedAction() {
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealDetails appealDetails = NonComplianceAppealDetails.builder()
                .files(Set.of(fileUuid))
                .registrationDate(LocalDate.now().minusDays(1))
                .comments("bla bla bla")
                .build();
        final String requestId = "requestId";
        final String regulatorAssignee = "bbb2820b-cbc6-4923-b3f1-8de409ea34c1";
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                .appealDetails(appealDetails)
                .regulatorAssignee(regulatorAssignee)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final NonComplianceAppealDetailsSubmittedRequestActionPayload requestActionPayload = NonComplianceAppealDetailsSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED_PAYLOAD)
                .appealDetails(appealDetails)
                .build();

        // invoke
        service.addAppealSubmittedAction(request);

        // verify
        verify(requestService, times(1)).addActionToRequest(
                request,
                requestActionPayload,
                CcaRequestActionType.NON_COMPLIANCE_APPEAL_DETAILS_SUBMITTED,
                regulatorAssignee);
    }
}
