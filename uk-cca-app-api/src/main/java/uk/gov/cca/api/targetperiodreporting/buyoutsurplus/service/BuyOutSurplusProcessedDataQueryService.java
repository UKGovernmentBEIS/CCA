package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusProcessedDataQueryService {

    private final BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    public Optional<Long> getBuyOutSurplusProcessedDataByPerformanceData(
    		final Long performanceDataId, final PerformanceDataResourceType resourceType) {
        return buyOutSurplusProcessedDataRepository.findByPerformanceDataIdAndPerformanceDataResourceType(performanceDataId, resourceType)
                .map(BuyOutSurplusProcessedData::getId);
    }
}
