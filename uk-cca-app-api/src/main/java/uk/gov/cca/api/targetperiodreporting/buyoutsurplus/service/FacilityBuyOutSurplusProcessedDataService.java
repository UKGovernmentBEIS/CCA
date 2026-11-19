package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleFacilitiesCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FacilityBuyOutSurplusProcessedDataService extends AbstractBuyOutSurplusProcessedDataService {

    private final BuyOutSurplusEligibleFacilitiesCustomRepository buyOutSurplusEligibleFacilitiesCustomRepository;

    public FacilityBuyOutSurplusProcessedDataService(
            BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository,
            TargetPeriodService targetPeriodService,
            BuyOutSurplusEligibleFacilitiesCustomRepository buyOutSurplusEligibleFacilitiesCustomRepository) {

        super(buyOutSurplusProcessedDataRepository, targetPeriodService);
        this.buyOutSurplusEligibleFacilitiesCustomRepository = buyOutSurplusEligibleFacilitiesCustomRepository;
    }

    public List<FacilityBuyOutSurplusProcessedDataInfo> getLatestBuyOutSurplusProcessedPrimaryPerformanceData(Set<Long> facilityIds, TargetPeriodType targetPeriodType) {
        validateTargetPeriodType(targetPeriodType);

        return buyOutSurplusProcessedDataRepository.findLatestBuyOutSurplusProcessedFacilitiesPrimaryPerformanceDataByTargetPeriodType(facilityIds, targetPeriodType);
    }

    public Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> getPerformanceDataWithBuyOutProcessedStatusPerFacility(Long accountId, List<Long> targetPeriodIds) {
        return buyOutSurplusEligibleFacilitiesCustomRepository
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds)
                .stream()
                .collect(Collectors.groupingBy(
                        FacilityPerformanceDataWithBuyOutProcessedStatus::getFacilityBusinessId,
                        Collectors.toMap(
                                FacilityPerformanceDataWithBuyOutProcessedStatus::getTargetPeriodType,
                                Function.identity(),
                                (existing, replacement) -> replacement,
                                () -> new TreeMap<>(Comparator.comparingInt(TargetPeriodType::getNumber)))
                ));
    }

    @Override
    protected PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.FACILITY;
    }
}
