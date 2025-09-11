package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityCertificationRunSummary {

    @NotNull
    @PositiveOrZero
    private Long totalAccounts;

    @NotNull
    @PositiveOrZero
    private Long failedAccounts;

    @NotNull
    @PositiveOrZero
    private Long facilitiesCertified;
}
