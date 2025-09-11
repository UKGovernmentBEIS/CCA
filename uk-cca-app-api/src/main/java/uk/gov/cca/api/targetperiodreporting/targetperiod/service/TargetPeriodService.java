package uk.gov.cca.api.targetperiodreporting.targetperiod.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.repository.TargetPeriodRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.transform.TargetPeriodMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TargetPeriodService {

  private final TargetPeriodRepository repository;
  private static final TargetPeriodMapper mapper = Mappers.getMapper(TargetPeriodMapper.class);

  @Transactional(readOnly = true)
  public TargetPeriodDTO getTargetPeriodByBusinessId(TargetPeriodType businessId) {
    Optional<TargetPeriod> entity = repository.findByBusinessId(businessId);
    return entity.map(mapper::toTargetPeriodDTO)
        .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
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
