package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

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
        BuyOutSurplusProcessedData entity = createEntity(performanceDataId, resourceType);

        buyOutSurplusProcessedDataRepository.save(entity);
    }
    
    @Transactional
    public void submitBuyOutSurplusProcessedDataBatch(Set<Long> performanceDataIds, PerformanceDataResourceType resourceType) {
        
        if (performanceDataIds.isEmpty()) {
            return;
        }

        List<BuyOutSurplusProcessedData> entities = performanceDataIds.stream()
                .map(performanceDataId -> createEntity(performanceDataId, resourceType))
                .toList();

        buyOutSurplusProcessedDataRepository.saveAll(entities);

    }
    
    private BuyOutSurplusProcessedData createEntity(Long performanceDataId, PerformanceDataResourceType resourceType) {
        return BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(resourceType)
                .build();
    }
}
