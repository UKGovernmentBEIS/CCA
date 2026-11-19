package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmissionsTradingSchemeDataSubmissionDetails {

    @NotNull
    private Year targetPeriodYear;

    @NotNull
    private LocalDateTime creationDate;

    @NotNull
    private LocalDateTime submissionDate;
}
