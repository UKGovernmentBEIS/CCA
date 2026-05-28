package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#complianceRestored) == (#complianceRestoredDate != null)}",
        message = "nonCompliance.nonComplianceConclusion.complianceRestoredDate")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#penaltyPaid) == (#penaltyPaymentDate != null)}",
        message = "nonCompliance.nonComplianceConclusion.penaltyPaymentDate")
public class NonComplianceConclusionDetails {

    @NotNull
    private Boolean complianceRestored;

    @PastOrPresent
    private LocalDate complianceRestoredDate;

    @NotNull
    private Boolean penaltyPaid;

    @PastOrPresent
    private LocalDate penaltyPaymentDate;

    @NotNull
    @Size(max = 10000)
    private String comment;

    @NotNull
    private NonCompliancePenaltyOutcomeType penaltyOutcome;
}
