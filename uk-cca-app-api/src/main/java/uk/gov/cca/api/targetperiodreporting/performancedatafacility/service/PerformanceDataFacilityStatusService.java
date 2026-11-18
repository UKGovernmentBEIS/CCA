package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacility;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateVariationIndicatorDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform.PerformanceDataFacilityMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;
import java.util.Set;

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
	
	@Transactional
	public void updateFacilityPerformanceDataLock(Long facilityId, FacilityPerformanceDataUpdateLockDTO updateLockDTO) {

		PerformanceDataFacilityStatus facilityPerformanceDataStatus = getFacilityPerformanceDataStatus(facilityId,
				updateLockDTO.getTargetPeriodYear());
		LocalDate secondaryReportingStartDate = targetPeriodService.getTargetPeriodByTargetPeriodTypeAndTargetYear(
				updateLockDTO.getTargetPeriodType(), updateLockDTO.getTargetPeriodYear()).getSecondaryReportingStartDate();
		
		// Current date should be after secondary reporting start date, and submission type should be FINAL
		if(LocalDate.now().isBefore(secondaryReportingStartDate) 
				|| !facilityPerformanceDataStatus.getLastPerformanceData().isFinal()) {
			throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_REPORT_UPDATE_NOT_VALID);
		}
		
		facilityPerformanceDataStatus.setLocked(updateLockDTO.getLocked());
	}
	
	@Transactional
	public void updateFacilityPerformanceDataVariationIndicator(Long facilityId,
			FacilityPerformanceDataUpdateVariationIndicatorDTO updateVariationIndicatorDTO) {
		
		PerformanceDataFacilityStatus facilityPerformanceDataStatus = getFacilityPerformanceDataStatus(facilityId,
				updateVariationIndicatorDTO.getTargetPeriodYear());
		
		facilityPerformanceDataStatus.setVariationIndicator(updateVariationIndicatorDTO.getVariationIndicator());
		
	}
	
	@Transactional
	public void updateFacilityPerformanceDataVariationIndicator(Set<String> eligibleFacilityBusinessIds,
			Year targetYear) {
		performanceDataFacilityStatusRepository.updateVariationIndicatorByFacilityBusinessIdInAndTargetPeriodYear(
				eligibleFacilityBusinessIds, targetYear);
		
	}

	private PerformanceDataFacilityStatus getFacilityPerformanceDataStatus(Long facilityId, Year year) {
		return performanceDataFacilityStatusRepository
				.findByFacilityIdAndTargetPeriodYear(facilityId, year)
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
	}
}
