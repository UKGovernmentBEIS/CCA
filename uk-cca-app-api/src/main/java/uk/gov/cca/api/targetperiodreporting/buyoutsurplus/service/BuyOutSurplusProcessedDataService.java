package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusProcessedDataService {

    private final BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;

    @Transactional
    public void submitBuyOutSurplusProcessedData(Long performanceDataId) {
        BuyOutSurplusProcessedData entity = BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .build();

        buyOutSurplusProcessedDataRepository.save(entity);
    }
}
