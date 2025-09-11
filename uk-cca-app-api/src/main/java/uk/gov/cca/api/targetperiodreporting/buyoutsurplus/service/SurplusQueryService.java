package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.SurplusRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.SurplusMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SurplusQueryService {

	private final SurplusRepository surplusRepository;
	private static final SurplusMapper SURPLUS_MAPPER = Mappers.getMapper(SurplusMapper.class);

	public List<SurplusGainedDTO> getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(Long accountId) {
		return surplusRepository.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId);
	}

	public Optional<SurplusDTO> getSurplusByAccountIdAndTargetPeriod(Long accountId, TargetPeriodType targetPeriodType) {
		return surplusRepository
				.findByAccountIdAndTargetPeriod_BusinessId(accountId, targetPeriodType)
				.map(SURPLUS_MAPPER::toSurplusDTO);
	}

	@Transactional(readOnly = true)
	public List<SurplusHistoryDTO> getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType targetPeriodType, Long accountId) {
		return surplusRepository.findByAccountIdAndTargetPeriod_BusinessId(accountId, targetPeriodType)
				.map(surplusEntity -> surplusEntity.getSurplusHistoryList().stream().map(SURPLUS_MAPPER::toSurplusHistoryDTO).toList())
				.orElse(Collections.emptyList());
	}
}
