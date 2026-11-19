package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusCalculationDetail {

    private Long id;

    private Long accountId;
    private Long facilityId;
    private Long targetPeriodId;
    private TargetPeriodType targetPeriodType;
    private Long performanceDataId;
    private LocalDateTime creationDate;
    
    private BuyOutSurplusCalculationDataContainer data;

}