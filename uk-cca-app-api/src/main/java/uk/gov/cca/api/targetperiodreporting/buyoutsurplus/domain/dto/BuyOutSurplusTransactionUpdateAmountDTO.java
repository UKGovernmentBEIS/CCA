package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyOutSurplusTransactionUpdateAmountDTO {

	@NotNull
	@Positive
	@Digits(integer = Integer.MAX_VALUE, fraction = 2)
	private BigDecimal amount;

	@NotBlank
	@Size(max = 10000)
	private String comments;

	@Builder.Default
	private Map<UUID, String> evidenceFiles = new HashMap<>();
}
