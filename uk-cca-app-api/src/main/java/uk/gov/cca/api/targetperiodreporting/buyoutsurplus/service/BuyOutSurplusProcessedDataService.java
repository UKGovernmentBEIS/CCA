package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusProcessedDataService {

    private final BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    @Transactional
    public void submitBuyOutSurplusProcessedData(Long performanceDataId, PerformanceDataResourceType resourceType) {
        BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(resourceType)
                .build();

        buyOutSurplusProcessedDataRepository.save(entity);
    }
}
