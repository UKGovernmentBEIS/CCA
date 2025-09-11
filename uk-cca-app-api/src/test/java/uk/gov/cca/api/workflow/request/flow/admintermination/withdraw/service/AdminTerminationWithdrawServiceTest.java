package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawSaveRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminTerminationWithdrawServiceTest {

    @InjectMocks
    private AdminTerminationWithdrawService adminTerminationWithdrawService;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final AdminTerminationWithdrawSaveRequestTaskActionPayload taskActionPayload =
                AdminTerminationWithdrawSaveRequestTaskActionPayload.builder()
                        .adminTerminationWithdrawReasonDetails(AdminTerminationWithdrawReasonDetails.builder().build())
                        .sectionsCompleted(sectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(AdminTerminationWithdrawRequestTaskPayload.builder()
                        .build())
                .build();

        // Invoke
        adminTerminationWithdrawService.applySaveAction(taskActionPayload, requestTask);

        // Verify
        AdminTerminationWithdrawRequestTaskPayload actual =
                (AdminTerminationWithdrawRequestTaskPayload) requestTask.getPayload();
        assertThat(actual.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

}
