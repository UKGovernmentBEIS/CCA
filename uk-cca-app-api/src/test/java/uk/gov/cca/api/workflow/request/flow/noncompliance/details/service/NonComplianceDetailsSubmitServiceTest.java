package uk.gov.cca.api.workflow.request.flow.noncompliance.details.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetails;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class NonComplianceDetailsSubmitServiceTest {

    @InjectMocks
    private NonComplianceDetailsSubmitService service;

    @Test
    void applySaveAction() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NonComplianceDetails nonComplianceDetails = NonComplianceDetails.builder()
                .nonComplianceType(NonComplianceType.FAILURE_TO_NOTIFY_OF_AN_ERROR)
                .nonCompliantDate(LocalDate.of(2025, 12, 12))
                .isEnforcementResponseNoticeRequired(false)
                .build();
        final NonComplianceDetailsSubmitSaveRequestTaskActionPayload actionPayload = NonComplianceDetailsSubmitSaveRequestTaskActionPayload.builder()
                .nonComplianceDetails(nonComplianceDetails)
                .sectionsCompleted(sectionsCompleted)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(NonComplianceDetailsSubmitRequestTaskPayload.builder().build())
                .build();

        // invoke
        service.applySaveAction(actionPayload, requestTask);

        // verify
        NonComplianceDetailsSubmitRequestTaskPayload requestTaskPayload = (NonComplianceDetailsSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getNonComplianceDetails()).isEqualTo(nonComplianceDetails);
        assertThat(requestTaskPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void submitDetails() {
        final Map<String, String> sectionsCompleted = Map.of("subtask", "in_progress");
        final NonComplianceDetails nonComplianceDetails = NonComplianceDetails.builder()
                .nonComplianceType(NonComplianceType.FAILURE_TO_NOTIFY_OF_AN_ERROR)
                .nonCompliantDate(LocalDate.of(2025, 12, 12))
                .isEnforcementResponseNoticeRequired(false)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(NonComplianceRequestPayload.builder().build())
                        .build())
                .payload(NonComplianceDetailsSubmitRequestTaskPayload.builder()
                        .nonComplianceDetails(nonComplianceDetails)
                        .sectionsCompleted(sectionsCompleted)
                        .build())
                .build();

        // invoke
        service.submitDetails(requestTask);

        // verify
        final Request request = requestTask.getRequest();
        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        assertThat(requestPayload.getNonComplianceDetails()).isEqualTo(nonComplianceDetails);
        assertThat(requestPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }
}
