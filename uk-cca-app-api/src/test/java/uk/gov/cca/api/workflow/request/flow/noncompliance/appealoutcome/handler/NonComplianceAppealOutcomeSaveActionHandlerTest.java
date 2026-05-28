package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealOutcomeSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain.NonComplianceAppealTribunalDecision;
import uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.service.NonComplianceAppealOutcomeService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceAppealOutcomeSaveActionHandlerTest {

    @InjectMocks
    private NonComplianceAppealOutcomeSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceAppealOutcomeService nonComplianceAppealOutcomeService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.NON_COMPLIANCE_APPEAL_OUTCOME_SAVE_APPLICATION;
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

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(nonComplianceAppealOutcomeService, times(1)).save(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_APPEAL_OUTCOME_SAVE_APPLICATION);
    }
}
