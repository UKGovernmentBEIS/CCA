package uk.gov.cca.api.facility.domain.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDataCreationDTO {

    @NotNull
    private String facilityId;

    @NotNull
    private Long accountId;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdDate;

    private LocalDate chargeStartDate;
}
