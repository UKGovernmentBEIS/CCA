package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.CorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditCorrectiveActionResponse;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AuditTrackCorrectiveActionsInitializerTest {

    @InjectMocks
    private AuditTrackCorrectiveActionsInitializer handler;

    @Test
    void initializePayload() {
        final Set<CorrectiveAction> actions = Set.of(CorrectiveAction.builder()
                        .title("Action1")
                        .deadline(LocalDate.of(2022, 5, 21))
                        .details("details1")
                        .build(),
                CorrectiveAction.builder()
                        .title("Action2")
                        .deadline(LocalDate.of(2022, 5, 21))
                        .details("details2")
                        .build());

        final Map<String, AuditCorrectiveActionResponse> correctiveActionResponses = actions.stream()
                .collect(Collectors.toMap(CorrectiveAction::getTitle, action -> AuditCorrectiveActionResponse.builder().action(action).build()));

        final FacilityAuditRequestPayload requestPayload = FacilityAuditRequestPayload.builder()
                .auditDetailsAndCorrectiveActions(AuditDetailsAndCorrectiveActions.builder()
                        .correctiveActions(CorrectiveActions.builder()
                                .actions(actions)
                                .build())
                        .build())
                .build();

        final Request request = Request.builder()
                .id("ADS_1-T00001-AUDT-1")
                .payload(requestPayload)
                .build();

        final AuditTrackCorrectiveActionsRequestTaskPayload expected =
                AuditTrackCorrectiveActionsRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.AUDIT_TRACK_CORRECTIVE_ACTIONS_PAYLOAD)
                        .auditTrackCorrectiveActions(AuditTrackCorrectiveActions.builder()
                                .correctiveActionResponses(correctiveActionResponses)
                                .build())
                        .sectionsCompleted(Map.of())
                        .build();
        // Invoke
        RequestTaskPayload actual = handler.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(AuditTrackCorrectiveActionsRequestTaskPayload.class)
                .isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.AUDIT_TRACK_CORRECTIVE_ACTIONS);
    }
}
