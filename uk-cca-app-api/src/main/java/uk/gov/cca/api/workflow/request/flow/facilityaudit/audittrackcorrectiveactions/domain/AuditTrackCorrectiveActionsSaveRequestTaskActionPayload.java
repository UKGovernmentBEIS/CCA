package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AuditTrackCorrectiveActionsSaveRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private String actionTitle;

    private CorrectiveActionFollowUpResponse correctiveActionFollowUpResponse;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
