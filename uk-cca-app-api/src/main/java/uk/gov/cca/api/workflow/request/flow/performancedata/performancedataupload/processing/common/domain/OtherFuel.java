package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtherFuel {

    private String name;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal conversionFactor;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 20)
    private BigDecimal consumption;
}