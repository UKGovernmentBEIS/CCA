package uk.gov.cca.api.sectorassociation.domain.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetCommitmentDTO {

    @NotBlank(message = "{targetCommitment.targetPeriod.notEmpty}")
    @Size(max = 255, message = "{targetCommitment.targetPeriod.size}")
    private String targetPeriod;

    private BigDecimal targetImprovement;
}
