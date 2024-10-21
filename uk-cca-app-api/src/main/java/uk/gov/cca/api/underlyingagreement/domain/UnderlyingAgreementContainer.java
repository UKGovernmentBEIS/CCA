package uk.gov.cca.api.underlyingagreement.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementContainer {

    @NotNull
    private MeasurementType sectorMeasurementType;
    
    private String sectorThroughputUnit;

    @NotNull
    @Valid
    private UnderlyingAgreement underlyingAgreement;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<UUID, String> underlyingAgreementAttachments = new HashMap<>();
}
