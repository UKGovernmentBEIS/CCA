package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusProcessedData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusProcessedDataRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractBuyOutSurplusProcessedDataService {

    protected final BuyOutSurplusProcessedDataRepository buyOutSurplusProcessedDataRepository;
    private final TargetPeriodService targetPeriodService;

    public Optional<Long> getBuyOutSurplusProcessedData(final Long performanceDataId) {
        return buyOutSurplusProcessedDataRepository.findByPerformanceDataIdAndPerformanceDataResourceType(performanceDataId, this.getResourceType())
                .map(BuyOutSurplusProcessedData::getId);
    }

    @Transactional
    public void submitBuyOutSurplusProcessedData(Long performanceDataId) {
        BuyOutSurplusProcessedData entity = createEntity(performanceDataId, this.getResourceType());

        buyOutSurplusProcessedDataRepository.save(entity);
    }

    @Transactional
    public void submitBuyOutSurplusProcessedDataBatch(Set<Long> performanceDataIds) {
        if (performanceDataIds.isEmpty()) {
            return;
        }

        List<BuyOutSurplusProcessedData> entities = performanceDataIds.stream()
                .map(performanceDataId -> createEntity(performanceDataId, this.getResourceType()))
                .toList();

        buyOutSurplusProcessedDataRepository.saveAll(entities);
    }

    protected abstract PerformanceDataResourceType getResourceType();

    protected void validateTargetPeriodType(TargetPeriodType targetPeriodType) {
        SchemeVersion schemeVersion = targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType).getSchemeVersion();
        if(schemeVersion.getResourceType() != this.getResourceType()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }

    private BuyOutSurplusProcessedData createEntity(Long performanceDataId, PerformanceDataResourceType resourceType) {
        return BuyOutSurplusProcessedData.builder()
                .performanceDataId(performanceDataId)
                .performanceDataResourceType(resourceType)
                .build();
    }
}
