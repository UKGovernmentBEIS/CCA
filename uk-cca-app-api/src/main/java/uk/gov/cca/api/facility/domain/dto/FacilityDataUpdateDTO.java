package uk.gov.cca.api.facility.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDataUpdateDTO {

    @NotNull
    private String facilityId;

    @PastOrPresent
    private LocalDate closedDate;

}
