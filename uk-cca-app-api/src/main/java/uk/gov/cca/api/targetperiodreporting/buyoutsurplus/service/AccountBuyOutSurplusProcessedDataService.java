package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

@Service
public class AccountBuyOutSurplusProcessedDataService extends AbstractBuyOutSurplusProcessedDataService {

    public AccountBuyOutSurplusProcessedDataService(
            BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository,
            TargetPeriodService targetPeriodService) {

        super(buyOutSurplusProcessedDataRepository, targetPeriodService);
    }

    @Override
    protected PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.ACCOUNT;
    }
}
