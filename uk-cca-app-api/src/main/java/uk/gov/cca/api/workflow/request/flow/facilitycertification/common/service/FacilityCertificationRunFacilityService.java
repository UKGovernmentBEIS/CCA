package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunFacilityService implements FacilityCertificationRunResourceTypeService {

    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final BuyOutSurplusProcessedDataQueryService buyOutSurplusProcessedDataQueryService;
    private final BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Override
    public Map<Long, FacilityCertificationAccountState> getEligibleAccounts(CertificationPeriodDTO certificationPeriodDetails) {
        return targetUnitAccountQueryService.getActiveAccounts().stream()
                .collect(Collectors.toMap(
                        TargetUnitAccountBusinessInfoDTO::getAccountId,
                        acc -> FacilityCertificationAccountState.builder()
                                .accountId(acc.getAccountId())
                                .accountBusinessId(acc.getBusinessId())
                                .build()
                ));
    }

    @Override
    public Set<Long> getFacilitiesEligibleForCertification(CertificationPeriodDTO certificationPeriodDetails, FacilityCertificationAccountState accountState) {
        Set<Long> facilitiesEligibleForCertification = new HashSet<>();
        final Set<Long> facilityIds = new HashSet<>(accountState.getFacilityIds());

        // Get all facilities processed
        Map<Long, Long> facilitiesProcessed = buyOutSurplusProcessedDataQueryService
                .getFacilitiesBuyOutSurplusProcessedData(facilityIds, certificationPeriodDetails.getTargetPeriodType()).stream()
                .collect(Collectors.toMap(
                        FacilityBuyOutSurplusProcessedDataInfo::getPerformanceDataId,
                        FacilityBuyOutSurplusProcessedDataInfo::getFacilityId
                ));
        if(!facilitiesProcessed.isEmpty()) {
            findEligibleProcessedFacilities(facilitiesProcessed, facilitiesEligibleForCertification);
        }

        // Facilities with no performance data get status from UNA
        Set<Long> facilitiesUnprocessed = SetUtils.difference(facilityIds, new HashSet<>(facilitiesProcessed.values()));
        if(!facilitiesUnprocessed.isEmpty()) {
            findEligibleUnprocessedFacilities(accountState.getAccountId(), facilitiesUnprocessed, facilitiesEligibleForCertification);
        }

        return facilitiesEligibleForCertification;
    }

    @Override
    public PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.FACILITY;
    }

    private void findEligibleProcessedFacilities(Map<Long, Long> facilitiesProcessed, Set<Long> facilitiesEligibleForCertification) {
        // Find facilities transactions
        Map<Long, BuyOutSurplusTransactionDetailInfoDTO> transactionDetails = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailsByPerformanceDataIds(facilitiesProcessed.keySet(), this.getResourceType()).stream()
                .collect(Collectors.toMap(BuyOutSurplusTransactionDetailInfoDTO::getPerformanceDataId, Function.identity()));

        transactionDetails.values().forEach(info -> {
            // If it has transaction as not AWAITING_PAYMENT then certify facilities
            if(!info.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)) {
                facilitiesEligibleForCertification.add(facilitiesProcessed.get(info.getPerformanceDataId()));
            }
        });

        // Certify facilities with no transaction details
        SetUtils.difference(facilitiesProcessed.keySet(), transactionDetails.keySet())
                .forEach(pd -> facilitiesEligibleForCertification.add(facilitiesProcessed.get(pd)));
    }

    private void findEligibleUnprocessedFacilities(Long accountId, Set<Long> facilityIds, Set<Long> facilitiesEligibleForCertification) {
        // TODO
    }
}
