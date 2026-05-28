package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform.PerformanceDataFacilityMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

import java.util.Optional;

@Validated
@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityStatusService {

    private final PerformanceDataFacilityStatusRepository performanceDataFacilityStatusRepository;
    private final PerformanceDataFacilityRepository performanceDataFacilityRepository;
    private final PerformanceDataFacilityStatusQueryService performanceDataFacilityStatusQueryService;
    private final TargetPeriodService targetPeriodService;
    private static final PerformanceDataFacilityMapper MAPPER = Mappers.getMapper(PerformanceDataFacilityMapper.class);

    @Transactional
    public int submitPerformanceData(@Valid PerformanceDataFacility newPerformanceData) {
        Optional<PerformanceDataFacilityStatus> performanceDataFacilityStatus = performanceDataFacilityStatusQueryService
                .getPerformanceDataFacilityStatus(newPerformanceData.getFacilityId(), newPerformanceData.getTargetPeriodYear());

        return performanceDataFacilityStatus
                .map(existing -> updateExistingPerformanceDataStatus(existing, newPerformanceData))
                .orElseGet(() -> createPerformanceData(newPerformanceData));

    }

    private int createPerformanceData(PerformanceDataFacility newPerformanceData) {
        final int reportVersion = 1;
        final TargetPeriod targetPeriod = targetPeriodService.findByTargetPeriodType(newPerformanceData.getTargetPeriodType());
        PerformanceDataFacilityEntity entity = performanceDataFacilityRepository
                .save(MAPPER.toPerformanceDataFacilityEntity(newPerformanceData, targetPeriod, reportVersion));

        PerformanceDataFacilityStatus statusEntity = PerformanceDataFacilityStatus.builder()
                .targetPeriod(targetPeriod)
                .targetPeriodYear(newPerformanceData.getTargetPeriodYear())
                .facilityId(newPerformanceData.getFacilityId())
                .lastPerformanceData(entity)
                .build();

        performanceDataFacilityStatusRepository.save(statusEntity);

        return reportVersion;
    }

    private int updateExistingPerformanceDataStatus(PerformanceDataFacilityStatus performanceDataFacilityStatus,
                                                     PerformanceDataFacility newPerformanceData) {
        final int reportVersion = performanceDataFacilityStatus.getLastPerformanceData().getReportVersion() + 1;
        final TargetPeriod targetPeriod = targetPeriodService.findByTargetPeriodType(newPerformanceData.getTargetPeriodType());
        PerformanceDataFacilityEntity entity = performanceDataFacilityRepository
                .save(MAPPER.toPerformanceDataFacilityEntity(newPerformanceData, targetPeriod, reportVersion));

        performanceDataFacilityStatus.setVariationIndicator(false);
        performanceDataFacilityStatus.setLastPerformanceData(entity);

        return reportVersion;
    }
}
