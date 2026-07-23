package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableTargetPeriodsBuyOutDTO {

	private List<TargetPeriodBuyOutDetailsDTO> currentTargetPeriods;
	private List<TargetPeriodBuyOutDetailsDTO> previousTargetPeriods;
}
