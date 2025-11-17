package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(T(java.lang.Boolean).TRUE.equals(#isActionCarriedOut) && #actionCarriedOutDate != null && #comments != null) " +
        "|| (T(java.lang.Boolean).FALSE.equals(#isActionCarriedOut) && (#actionCarriedOutDate == null))}",
        message = "facilityAudit.trackcorrectiveactions.correctiveActionFollowUpResponse")
public class CorrectiveActionFollowUpResponse {

    @NotNull
    private Boolean isActionCarriedOut;

    @PastOrPresent
    private LocalDate actionCarriedOutDate;

    @Size(max = 10000)
    private String comments;

    @Builder.Default
    private Set<UUID> evidenceFiles = new HashSet<>();
}
