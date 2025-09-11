package uk.gov.cca.api.subsistencefees.domain.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubsistenceFeesMoaReceivedAmountDetailsDTO {

    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @NotNull
    private BigDecimal transactionAmount;

    @Size(max = 10000)
    private String comments;

    @Builder.Default
    private Map<UUID, String>  evidenceFiles = new HashMap<>();
}
