package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cca3FacilityBaselineAndTargets {

    @NotNull
    @Valid
    private FacilityTargetComposition targetComposition;

    @NotNull
    @Valid
    private FacilityBaselineData baselineData;

    @NotNull
    @Valid
    private FacilityTargets facilityTargets;
}
