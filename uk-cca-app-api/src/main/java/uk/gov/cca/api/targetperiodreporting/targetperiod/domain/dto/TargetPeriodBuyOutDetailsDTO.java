package uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetPeriodBuyOutDetailsDTO {

	private Long id;
    private TargetPeriodType businessId;
    private Integer buyOutCost;
}
