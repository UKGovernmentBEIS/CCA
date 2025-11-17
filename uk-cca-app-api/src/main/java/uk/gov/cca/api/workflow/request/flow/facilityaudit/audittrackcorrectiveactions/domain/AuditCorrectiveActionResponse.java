package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.CorrectiveAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditCorrectiveActionResponse {

    @Valid
    @NotNull
    @JsonUnwrapped
    CorrectiveAction action;

    @Valid
    @NotNull
    @JsonUnwrapped
    CorrectiveActionFollowUpResponse response;
}
