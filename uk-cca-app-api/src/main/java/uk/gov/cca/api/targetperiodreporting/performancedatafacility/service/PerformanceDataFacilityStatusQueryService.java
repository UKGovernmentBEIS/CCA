package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityStatus;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform.PerformanceDataFacilityDetailsMapper;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform.PerformanceDataFacilityStatusMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityStatusQueryService {

    private final PerformanceDataFacilityStatusRepository performanceDataFacilityStatusRepository;
    private final TargetPeriodService targetPeriodService;
    private static final PerformanceDataFacilityStatusMapper PERFORMANCE_DATA_FACILITY_STATUS_MAPPER = Mappers.getMapper(PerformanceDataFacilityStatusMapper.class);
    private static final PerformanceDataFacilityDetailsMapper PERFORMANCE_DATA_FACILITY_DETAILS_MAPPER = Mappers.getMapper(PerformanceDataFacilityDetailsMapper.class);
    
    public Optional<PerformanceDataFacilityStatus> getPerformanceDataFacilityStatus(Long facilityId, Year targetPeriodYear) {
        return performanceDataFacilityStatusRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    public Optional<PerformanceDataFacilityContainer> getLastUploadedPerformanceDataContainer(Long facilityId, Year targetPeriodYear) {
        return getPerformanceDataFacilityStatus(facilityId, targetPeriodYear)
                .map(PerformanceDataFacilityStatus::getLastPerformanceData)
                .map(PerformanceDataFacilityEntity::getData);
    }

    public boolean getLockedStatus(Long facilityId, Year targetPeriodYear) {
        return getPerformanceDataFacilityStatus(facilityId, targetPeriodYear)
                .map(PerformanceDataFacilityStatus::isLocked).orElse(false);
    }

	public List<FacilityPerformanceDataStatusInfoDTO> getFacilityPerformanceDataStatusInfo(Long facilityId, TargetPeriodType targetPeriodType,
			PerformanceDataReportType reportType, AppUser currentUser) {
		LocalDate secondaryReportingStartDate = targetPeriodService.findByTargetPeriodType(targetPeriodType).getSecondaryReportingStartDate();
		
		return performanceDataFacilityStatusRepository
				.findWithDetailsByFacilityIdAndTargetPeriodBusinessId(facilityId, targetPeriodType)
				.stream()
				.filter(entity -> entity.getLastPerformanceData().hasReportType(reportType))
				.map(entity -> PERFORMANCE_DATA_FACILITY_STATUS_MAPPER
						.toFacilityPerformanceDataStatusInfoDTO(entity, secondaryReportingStartDate, currentUser.getRoleType()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public FacilityPerformanceDataReportDetailsDTO getFacilityPerformanceDataReportDetails(Long facilityId, Year targetPeriodYear) {
		PerformanceDataFacilityEntity performanceData = getLastPerformanceData(facilityId, targetPeriodYear);
		
		return PERFORMANCE_DATA_FACILITY_DETAILS_MAPPER.toFacilityPerformanceDataReportDetailsDTO(
				performanceData.getData(), performanceData.getTargetPeriod().getBusinessId());
	}
	
	@Transactional(readOnly = true)
	public Set<Long> getLockedFacilityIds(Set<Long> facilityIds, Year year) {
		return getFacilityPerformanceDataStatusByFacilityIdInAndLockedStatus(facilityIds, year, true).stream()
				.map(PerformanceDataFacilityStatus::getFacilityId)
				.collect(Collectors.toSet());
	}
	
	public List<PerformanceDataFacilityStatus> getFacilityPerformanceDataStatusByFacilityIdInAndLockedStatus(Set<Long> facilityIds, Year targetPeriodYear, boolean locked) {
		return performanceDataFacilityStatusRepository.findByFacilityIdInAndTargetPeriodYearAndLocked(facilityIds, targetPeriodYear, locked);
	}
	
	private PerformanceDataFacilityEntity getLastPerformanceData(Long facilityId, Year targetPeriodYear) {
        return this.getFacilityPerformanceDataStatus(facilityId, targetPeriodYear)
                .getLastPerformanceData();
    }

    private PerformanceDataFacilityStatus getFacilityPerformanceDataStatus(Long facilityId, Year targetPeriodYear) {
        return performanceDataFacilityStatusRepository
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

}
