package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusProcessedDataQueryService {

    private final BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    public Optional<Long> getBuyOutSurplusProcessedDataByPerformanceData(final Long performanceDataId) {
        return buyOutSurplusProcessedDataRepository.findByPerformanceDataId(performanceDataId)
                .map(BuyOutSurplusProcessedData::getId);
    }
}
