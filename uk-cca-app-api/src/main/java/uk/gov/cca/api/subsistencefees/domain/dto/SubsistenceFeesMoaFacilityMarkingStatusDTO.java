package uk.gov.cca.api.subsistencefees.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubsistenceFeesMoaFacilityMarkingStatusDTO {

    @NotNull
    private FacilityPaymentStatus status;

    @Builder.Default
    @NotNull
    private Set<Long> filterResourceIds = new HashSet<>();
}
