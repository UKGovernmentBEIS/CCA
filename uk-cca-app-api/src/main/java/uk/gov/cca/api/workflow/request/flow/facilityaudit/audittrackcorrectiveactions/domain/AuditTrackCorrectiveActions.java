package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTrackCorrectiveActions {

    @NotEmpty
    @Builder.Default
    private Map<String, @Valid @NotNull AuditCorrectiveActionResponse> correctiveActionResponses = new LinkedHashMap<>();
}
