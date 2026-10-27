package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunAccountService implements FacilityCertificationRunResourceTypeService {

    private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;
    private final BuyOutSurplusProcessedDataQueryService buyOutSurplusProcessedDataQueryService;
    private final BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Override
    public Map<Long, FacilityCertificationAccountState> getEligibleAccounts(CertificationPeriodDTO certificationPeriodDetails) {
        return accountPerformanceDataStatusQueryService
                .findAccountsWithPerformanceDataForTargetPeriod(certificationPeriodDetails.getTargetPeriodType()).stream()
                .collect(Collectors.toMap(
                        AccountPerformanceDataInfo::getAccountId,
                        acc -> FacilityCertificationAccountState.builder()
                                .accountId(acc.getAccountId())
                                .accountBusinessId(acc.getAccountBusinessId())
                                .lastPerformanceDataId(acc.getLastPerformanceDataId())
                                .build()
                ));
    }

    @Override
    public Set<Long> getFacilitiesEligibleForCertification(CertificationPeriodDTO certificationPeriodDetails, FacilityCertificationAccountState accountState) {
        Set<Long> facilitiesEligibleForCertification = new HashSet<>();
        final Long performanceDataId = accountState.getLastPerformanceDataId();

        // Find if buy out surplus has been run for account's last performance data
        buyOutSurplusProcessedDataQueryService.getAccountBuyOutSurplusProcessedDataByPerformanceData (performanceDataId)
                .ifPresent(processedData ->
                        // If it has no transaction or transaction as not AWAITING_PAYMENT then certify facilities
                        buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailByPerformanceData(accountState.getLastPerformanceDataId(), this.getResourceType())
                                .ifPresentOrElse(info -> {
                                    // If it has transaction as not AWAITING_PAYMENT then certify facilities
                                    if(!info.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)) {
                                        facilitiesEligibleForCertification.addAll(accountState.getFacilityIds());
                                    }
                                }, () -> facilitiesEligibleForCertification.addAll(accountState.getFacilityIds()))
                );

        return facilitiesEligibleForCertification;
    }

    @Override
    public PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.ACCOUNT;
    }
}
