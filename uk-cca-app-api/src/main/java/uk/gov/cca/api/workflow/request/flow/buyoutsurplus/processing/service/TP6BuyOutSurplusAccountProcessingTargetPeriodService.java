package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;

@Service
@RequiredArgsConstructor
public class TP6BuyOutSurplusAccountProcessingTargetPeriodService implements BuyOutSurplusAccountProcessingTargetPeriodService {

    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

    @Transactional
    @Override
    public void processBuyOutSurplus(BuyOutSurplusAccountState accountState) throws Exception {
        // Get last performance data
        PerformanceBuyOutSurplusDetailsDTO performanceReportDetails = accountPerformanceDataStatusQueryService
                .getLastPerformanceBuyOutSurplusDetails(accountState.getAccountId(), TargetPeriodType.TP6);
        // TODO
    }

    @Override
    public TargetPeriodType getType() {
        return TargetPeriodType.TP6;
    }
}
