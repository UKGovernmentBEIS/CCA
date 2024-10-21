package uk.gov.cca.api.sectorassociation.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{" +
                "((#targetCurrencyType eq 'Absolute' || #targetCurrencyType eq 'Relative') == (#throughputUnit != null))"+
                "}",
        message = "targetSet.throughputUnit.message"
)
public class TargetSetDTO {

    @NotNull
    private Long id;

    @NotBlank(message = "{targetSet.targetCurrencyType.notEmpty}")
    @Size(max = 255, message = "{targetSet.targetCurrencyType.size}")
    private String targetCurrencyType;

    @Size(max = 255, message = "{targetSet.throughputUnit.size}")
    private String throughputUnit;

    @NotBlank(message = "{targetSet.energyOrCarbonUnit.notEmpty}")
    @Size(max = 255, message = "{targetSet.energyOrCarbonUnit.size}")
    private String energyOrCarbonUnit;

    private List<TargetCommitmentDTO> targetCommitments;
}
