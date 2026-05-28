package uk.gov.cca.api.workflow.request.flow.noncompliance.appealoutcome.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NonComplianceAppealOutcomeDetails {

    @NotNull
    private NonComplianceAppealTribunalDecision tribunalDecision;

    @NotNull
    @PastOrPresent
    private LocalDate appealOutcomeDate;

    private UUID file;

    @Size(max = 10000)
    private String comments;
}
