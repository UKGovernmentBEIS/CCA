package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;

import java.time.Year;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityStatusQueryService {

    private final PerformanceDataFacilityStatusRepository performanceDataFacilityStatusRepository;

    public Optional<PerformanceDataFacilityStatus> getPerformanceDataFacilityStatus(Long facilityId, Year targetPeriodYear) {
        return performanceDataFacilityStatusRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    public Optional<PerformanceDataFacilityContainer> getLastUploadedPerformanceDataContainer(Long facilityId, Year targetPeriodYear) {
        return getPerformanceDataFacilityStatus(facilityId, targetPeriodYear)
                .map(PerformanceDataFacilityStatus::getLastPerformanceData)
                .map(PerformanceDataFacilityEntity::getData);
    }
}
