package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusExclusion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusExclusionService {

	private final BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;
	private final SurplusQueryService surplusQueryService;

	public AccountBuyOutSurplusInfoDTO getBuyOutSurplusInfoByAccountId(Long accountId) {

		boolean excluded = buyOutSurplusExclusionRepository.existsByAccountId(accountId);
		return AccountBuyOutSurplusInfoDTO.builder()
				.excluded(excluded)
				.surplusGainedDTOList(this.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId))
				.build();
	}

	public boolean isAccountExcludedFromBuyOutSurplus(Long accountId) {
		return buyOutSurplusExclusionRepository.existsByAccountId(accountId);
	}

	@Transactional
	public void excludeAccountFromBuyOutSurplus(Long accountId) {
		if (!isAccountExcludedFromBuyOutSurplus(accountId)) {
			buyOutSurplusExclusionRepository.save(BuyOutSurplusExclusion.builder().accountId(accountId).build());
		}
	}

	@Transactional
	public void removeAccountExclusionFromBuyOutSurplus(Long accountId) {
		buyOutSurplusExclusionRepository.deleteByAccountId(accountId);
	}

	private List<SurplusGainedDTO> getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(Long accountId) {

		// TODO: List of SurplusGainedDTO needs to be dynamically configured in CCA3
		final List<SurplusGainedDTO> surplusGainedDTOList = surplusQueryService
				.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId);
		if (surplusGainedDTOList.stream()
				.noneMatch(surplusGainedDTO -> surplusGainedDTO.getTargetPeriod().equals(TargetPeriodType.TP6))) {
			surplusGainedDTOList.add(SurplusGainedDTO.builder()
					.surplusGained(BigDecimal.ZERO)
					.targetPeriod(TargetPeriodType.TP6)
					.hasHistory(false)
					.build());
		}

		return surplusGainedDTOList;
	}
}
