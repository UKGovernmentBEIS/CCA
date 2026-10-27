package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.SurplusMapper;

@Service
@RequiredArgsConstructor
public class SurplusQueryService {

    private final SurplusRepository surplusRepository;
    private static final SurplusMapper SURPLUS_MAPPER = Mappers.getMapper(SurplusMapper.class);

    @Transactional(readOnly = true)
    public SurplusGainedDTO getAccountSurplusGainedWithHistoryFlag(Long accountId) {
        return surplusRepository.findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2)
                .map(SURPLUS_MAPPER::toSurplusGainedDTO)
                .orElse(SurplusGainedDTO.builder()
                        .schemeVersion(SchemeVersion.CCA_2)
                        .surplusGained(BigDecimal.ZERO)
                        .hasHistory(false)
                        .build());
    }

    @Transactional(readOnly = true)
    public SurplusGainedDTO getFacilitySurplusGainedWithHistoryFlag(Long facilityId) {
        return surplusRepository.findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3)
                .map(SURPLUS_MAPPER::toSurplusGainedDTO)
                .orElse(SurplusGainedDTO.builder()
                        .schemeVersion(SchemeVersion.CCA_3)
                        .surplusGained(BigDecimal.ZERO)
                        .hasHistory(false)
                        .build());
    }

    public Optional<SurplusDTO> getSurplusByResourceIdAndSchemeVersion(Long resourceId, SchemeVersion schemeVersion) {
        return surplusRepository.findByResourceIdAndSchemeVersion(resourceId, schemeVersion)
                .map(SURPLUS_MAPPER::toSurplusDTO);
    }

    @Transactional(readOnly = true)
    public List<SurplusHistoryDTO> getAccountSurplusHistory(Long accountId) {
        List<SurplusHistory> surplusHistories = surplusRepository.findByResourceIdAndSchemeVersion(accountId, SchemeVersion.CCA_2)
                .map(SurplusEntity::getSurplusHistoryList).orElse(List.of());

        return surplusHistories.stream().map(SURPLUS_MAPPER::toSurplusHistoryDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<SurplusHistoryDTO> getFacilitySurplusHistory(Long facilityId) {
        List<SurplusHistory> surplusHistories = surplusRepository.findByResourceIdAndSchemeVersion(facilityId, SchemeVersion.CCA_3)
                .map(SurplusEntity::getSurplusHistoryList).orElse(List.of());

        return surplusHistories.stream().map(SURPLUS_MAPPER::toSurplusHistoryDTO).toList();
    }

    public Map<Long, BigDecimal> getFacilityPositiveSurplusMap(List<Long> facilityIds, SchemeVersion schemeVersion) {
        return surplusRepository
                .findAllByResourceIdInAndSchemeVersion(facilityIds, schemeVersion)
                .stream()
                .filter(entry -> entry.getSurplusGained().compareTo(BigDecimal.ZERO) > 0 )
                .collect(Collectors.toMap(SurplusEntity::getResourceId, SurplusEntity::getSurplusGained));
    }
}
