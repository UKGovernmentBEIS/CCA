package uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityCertificationDTO {
    
    @NotNull
    private Long facilityId;

    @NotNull
    private Long certificationPeriodId;

    @NotNull
    private FacilityCertificationStatus certificationStatus;

    @PastOrPresent
    private LocalDate startDate;
}
