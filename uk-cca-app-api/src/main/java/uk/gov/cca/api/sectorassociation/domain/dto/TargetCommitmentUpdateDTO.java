package uk.gov.cca.api.sectorassociation.domain.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetCommitmentUpdateDTO {

    @NotNull
    private Long id;

    @NotNull
    @DecimalMax(value = "99.999")
    @DecimalMin(value = "-99.999")
    @Digits(integer = 2, fraction = 3)
    private BigDecimal targetImprovement;
}
