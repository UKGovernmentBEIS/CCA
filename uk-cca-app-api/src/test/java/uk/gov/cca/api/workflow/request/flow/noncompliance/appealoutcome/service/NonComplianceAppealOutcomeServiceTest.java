package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealTribunalDecision;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealOutcomeServiceTest {

    @InjectMocks
    private NonComplianceAppealOutcomeService service;

    @Test
    void save() {
        final long requestTaskId = 1L;
        final UUID fileUuid = UUID.randomUUID();
        final NonComplianceAppealOutcomeDetails appealOutcome = NonComplianceAppealOutcomeDetails.builder()
                .file(fileUuid)
                .appealOutcomeDate(LocalDate.now())
                .tribunalDecision(NonComplianceAppealTribunalDecision.APPEAL_ALLOWED)
                .comments("bla bla")
                .build();
        final NonComplianceAppealOutcomeSaveRequestTaskActionPayload taskActionPayload =
                NonComplianceAppealOutcomeSaveRequestTaskActionPayload.builder().appealOutcome(appealOutcome).build();

        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .payload(NonComplianceAppealOutcomeSubmitRequestTaskPayload.builder().build())
                .build();

        // invoke
        service.save(taskActionPayload, requestTask);

        // verify
        NonComplianceAppealOutcomeSubmitRequestTaskPayload requestTaskPayload = (NonComplianceAppealOutcomeSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getAppealOutcome()).isEqualTo(appealOutcome);
    }

    @Test
    void complete() {
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
        final UUID fileUuid = UUID.randomUUID();
        final String regulatorAssignee = appUser.getUserId();

        final NonComplianceAppealOutcomeDetails appealOutcome = NonComplianceAppealOutcomeDetails.builder()
                .file(fileUuid)
                .appealOutcomeDate(LocalDate.now())
                .tribunalDecision(NonComplianceAppealTribunalDecision.APPEAL_ALLOWED)
                .comments("bla bla")
                .build();

        final NonComplianceAppealOutcomeSubmitRequestTaskPayload requestTaskPayload = NonComplianceAppealOutcomeSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_APPEAL_OUTCOME_SUBMIT_PAYLOAD)
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .build();

        final NonComplianceRequestPayload expected = NonComplianceRequestPayload.builder()
                .appealOutcome(appealOutcome)
                .nonComplianceAttachments(Map.of(fileUuid, "file"))
                .regulatorAssignee(regulatorAssignee)
                .build();

        final Request request = Request.builder()
                .id(requestId)
                .payload(NonComplianceRequestPayload.builder().build())
                .build();

        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        // invoke
        service.complete(requestTask);

        // verify
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload).isEqualTo(expected);
    }
}
