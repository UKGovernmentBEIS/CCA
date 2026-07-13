package uk.gov.cca.api.targetperiodreporting.targetperiod.service;

import java.time.Year;
import java.util.List;
import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.repository.TargetPeriodRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.transform.TargetPeriodMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TargetPeriodService {

    private final TargetPeriodRepository repository;
    private static final TargetPeriodMapper MAPPER = Mappers.getMapper(TargetPeriodMapper.class);

    public List<TargetPeriodDetailsDTO> getTargetPeriodDetailsBySchemeVersion(SchemeVersion schemeVersion) {
        return repository.findAllBySchemeVersion(schemeVersion).stream().map(MAPPER::toTargetPeriodDetailsDTO).toList();
    }

    public List<TargetPeriodDetailsDTO> getTargetPeriodDetailsByTargetPeriodTypes(Set<TargetPeriodType> targetPeriodTypes) {
        return repository.findByBusinessIdIn(targetPeriodTypes).stream().map(MAPPER::toTargetPeriodDetailsDTO).toList();
    }

    public TargetPeriodDetailsDTO getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return MAPPER.toTargetPeriodDetailsDTO(this.getByTargetPeriodType(targetPeriodType));
    }

    public TargetPeriodInfoDTO getTargetPeriodInfoByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return MAPPER.toTargetPeriodInfoDTO(this.getByTargetPeriodType(targetPeriodType));
    }

    public TargetPeriodYearDTO getTargetPeriodByTargetPeriodTypeAndTargetYear(TargetPeriodType targetPeriodType, Year targetYear) {
        return MAPPER.toTargetPeriodDTO(this.getByTargetPeriodType(targetPeriodType), targetYear);
    }
    
	public TargetPeriodYear getTargetPeriodYearByTargetPeriodTypeAndTargetYear(TargetPeriodType targetPeriodType,Year targetYear) {
		return this.getByTargetPeriodType(targetPeriodType).getTargetPeriodYearsContainer().getTargetPeriodYear(targetYear)
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
	}

    public String findTargetPeriodNameByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return this.getByTargetPeriodType(targetPeriodType).getName();
    }

    public TargetPeriod findByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return getByTargetPeriodType(targetPeriodType);
    }
    
    public List<TargetPeriodYear> getTargetPeriodYearsByTypeAndReportType(TargetPeriodType targetPeriodType, String reportType) {
		TargetPeriodYearsContainer container = getByTargetPeriodType(targetPeriodType).getTargetPeriodYearsContainer();

		TargetPeriodYear finalTargetYear = container.getTargetPeriodYear(container.getFinalTargetPeriodTargetYear())
				.orElse(null);

		if (reportType.equals("FINAL")) {
			return finalTargetYear == null ? List.of() : List.of(finalTargetYear);
		}

		return container.getTargetPeriodYears().stream()
				.filter(year -> !year.equals(finalTargetYear))
				.toList();
	}  

    private TargetPeriod getByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return repository.findByBusinessId(targetPeriodType)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
