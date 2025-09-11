package uk.gov.cca.api.underlyingagreement.domain.facilities;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityTargetComposition {

    @NotNull
    private UUID calculatorFile;

    @NotNull
    private MeasurementType measurementType;

    @NotNull
    private AgreementCompositionType agreementCompositionType;
}
