package uk.gov.cca.api.web.orchestrator.account.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusExclusionService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.authorization.core.domain.AppUser;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountBuyOutSurplusServiceOrchestrator {

	private final BuyOutSurplusExclusionService buyOutSurplusExclusionService;

	private final SurplusQueryService surplusQueryService;

	private final SurplusService surplusService;

	public AccountBuyOutSurplusInfoDTO getBuyOutSurplusInfoByAccountId(Long accountId) {

		boolean excluded = buyOutSurplusExclusionService.isAccountExcludedFromBuyOutSurplus(accountId);
		return AccountBuyOutSurplusInfoDTO.builder()
				.excluded(excluded)
				.surplusGainedDTOList(this.getListOfTargetPeriodSurplusGainedWithHistoryFlagByAccountId(accountId))
				.build();
	}

	public void updateSurplusGained(SurplusUpdateDTO surplusUpdateDTO, Long accountId, AppUser appUser) {

		surplusService.updateSurplusGained(surplusUpdateDTO, accountId, appUser);
	}

	public List<SurplusHistoryDTO> getAllSurplusHistoryByTargetPeriodAndAccountId(TargetPeriodType targetPeriodType,
	                                                                              Long accountId) {
		return surplusQueryService.getAllSurplusHistoryByAccountIdAndTargetPeriod(targetPeriodType,accountId);
	}

	@Transactional
	public void excludeAccountFromBuyOutSurplus(Long accountId) {
		buyOutSurplusExclusionService.excludeAccountFromBuyOutSurplus(accountId);
	}

	@Transactional
	public void removeAccountExclusionFromBuyOutSurplus(Long accountId) {
		buyOutSurplusExclusionService.removeAccountExclusionFromBuyOutSurplus(accountId);
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
