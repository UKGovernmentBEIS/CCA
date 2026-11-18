package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurplusGainedDTO {

	private TargetPeriodType targetPeriod;

	private BigDecimal surplusGained;

	private boolean hasHistory;

}
