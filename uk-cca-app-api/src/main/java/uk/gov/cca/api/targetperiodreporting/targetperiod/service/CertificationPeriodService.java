package uk.gov.cca.api.targetperiodreporting.targetperiod.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.repository.CertificationPeriodRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.transform.CertificationPeriodMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificationPeriodService {

    private final CertificationPeriodRepository certificationPeriodRepository;
    private static final CertificationPeriodMapper MAPPER = Mappers.getMapper(CertificationPeriodMapper.class);

    public List<CertificationPeriodDTO> getAllCertificationPeriods() {
        return certificationPeriodRepository.findAll().stream()
                .map(MAPPER::toCertificationPeriodDTO).toList();
    }

    public CertificationPeriodDTO getCertificationPeriodByType(CertificationPeriodType type) {
        return certificationPeriodRepository.findByBusinessId(type)
                .map(MAPPER::toCertificationPeriodDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public CertificationPeriodDTO getCertificationPeriodByTriggerDate(LocalDate triggerDate) {
        return certificationPeriodRepository.findByCertificationBatchTriggerDate(triggerDate)
                .map(MAPPER::toCertificationPeriodDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public CertificationPeriodInfoDTO getCurrentCertificationPeriod() {
        return getCurrentCertificationPeriodOptional()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public Optional<CertificationPeriodInfoDTO> getCurrentCertificationPeriodOptional() {
        return certificationPeriodRepository.findCertificationPeriodByDate(LocalDate.now())
                .map(MAPPER::toCertificationPeriodInfoDTO);
    }

    public CertificationPeriodDTO getCertificationPeriodById(Long certificationPeriodId) {
        return certificationPeriodRepository.findById(certificationPeriodId)
                .map(MAPPER::toCertificationPeriodDTO)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
