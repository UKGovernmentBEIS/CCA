package uk.gov.cca.api.subsistencefees.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.subsistencefees.domain.MoaType;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityProcessStatusCreationDTO {

    @NotNull
    private Long facilityId;

    @NotNull
    private Year chargingYear;

    @NotNull
    private Long runId;

    @NotNull
    private MoaType moaType;
}
