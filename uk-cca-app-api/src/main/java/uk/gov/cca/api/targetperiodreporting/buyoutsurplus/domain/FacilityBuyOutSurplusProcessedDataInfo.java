package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityBuyOutSurplusProcessedDataInfo {
    private Long facilityId;
    private Long performanceDataId;
}
