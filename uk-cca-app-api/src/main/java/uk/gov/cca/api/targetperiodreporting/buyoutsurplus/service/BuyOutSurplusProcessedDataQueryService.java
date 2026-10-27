package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusProcessedDataQueryService {

    private final BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    public Optional<Long> getAccountBuyOutSurplusProcessedDataByPerformanceData(final Long performanceDataId) {
        return buyOutSurplusProcessedDataRepository.findByPerformanceDataIdAndPerformanceDataResourceType(performanceDataId, PerformanceDataResourceType.ACCOUNT)
                .map(BuyOutSurplusProcessedData::getId);
    }

    public List<FacilityBuyOutSurplusProcessedDataInfo> getFacilitiesBuyOutSurplusProcessedData(Set<Long> facilityIds, TargetPeriodType targetPeriodType) {
        return buyOutSurplusProcessedDataRepository.findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType(facilityIds, targetPeriodType);
    }
}
