package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementSection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetPeriod6Details implements UnderlyingAgreementSection {

    @NotNull
    @Valid
    private TargetComposition targetComposition;

    @NotNull
    @Valid
    private BaselineData baselineData;
    
    @NotNull
    @Valid
    private Targets targets;
}
