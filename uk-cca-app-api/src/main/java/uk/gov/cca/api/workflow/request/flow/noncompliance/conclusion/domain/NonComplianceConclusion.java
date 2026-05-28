package uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#details.penaltyOutcome eq 'WITHDRAW') == (#withdrawNotice != null)}",
        message = "nonCompliance.nonComplianceConclusion.withdrawNotice")
public class NonComplianceConclusion {

    @Valid
    @NotNull
    private NonComplianceConclusionDetails details;

    @Valid
    private NonComplianceWithdrawNotice withdrawNotice;
}
