package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.List;

public interface BuyOutSurplusQueryService {

    List<TargetUnitAccountBusinessInfoDTO> getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(TargetPeriodType targetPeriodType);

    PerformanceDataResourceType getResourceType();
}
