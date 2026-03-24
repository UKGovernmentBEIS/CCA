package uk.gov.cca.api.targetperiodreporting.targetperiod.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.repository.TargetPeriodRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.transform.TargetPeriodMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class TargetPeriodService {

    private final TargetPeriodRepository repository;
    private static final TargetPeriodMapper MAPPER = Mappers.getMapper(TargetPeriodMapper.class);

    @Transactional(readOnly = true)
    public TargetPeriodDetailsDTO getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return MAPPER.toTargetPeriodDetailsDTO(this.getByTargetPeriodType(targetPeriodType));
    }

    @Transactional(readOnly = true)
    public TargetPeriodInfoDTO getTargetPeriodInfoByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return MAPPER.toTargetPeriodInfoDTO(this.getByTargetPeriodType(targetPeriodType));
    }

    @Transactional(readOnly = true)
    public TargetPeriodYearDTO getTargetPeriodByTargetPeriodTypeAndTargetYear(TargetPeriodType targetPeriodType, Year targetYear) {
        return MAPPER.toTargetPeriodDTO(this.getByTargetPeriodType(targetPeriodType), targetYear);
    }

    @Transactional(readOnly = true)
    public String findTargetPeriodNameByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return this.getByTargetPeriodType(targetPeriodType).getName();
    }

    @Transactional(readOnly = true)
    public TargetPeriod findByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return getByTargetPeriodType(targetPeriodType);
    }

    private TargetPeriod getByTargetPeriodType(TargetPeriodType targetPeriodType) {
        return repository.findByBusinessId(targetPeriodType)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
