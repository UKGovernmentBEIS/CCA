package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestPayload;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunFacilityService implements FacilityCertificationRunResourceTypeService {

    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final FacilityBuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;
    private final FacilityBuyOutSurplusQueryService buyOutSurplusQueryService;

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
    public Set<Long> getFacilitiesEligibleForCertification(final FacilityCertificationAccountProcessingRequestPayload payload,
                                                           final FacilityCertificationAccountState accountState) {
        Set<Long> facilitiesEligibleForCertification = new HashSet<>();
        final Set<Long> facilityIds = new HashSet<>(accountState.getFacilityIds());
        final TargetPeriodInfoDTO targetPeriod = payload.getTargetPeriod();

        // Get all facilities processed
        Map<Long, Long> facilitiesProcessed = buyOutSurplusProcessedDataService
                .getLatestBuyOutSurplusProcessedPrimaryPerformanceData(facilityIds, targetPeriod.getBusinessId()).stream()
                .collect(Collectors.toMap(
                        FacilityBuyOutSurplusProcessedDataInfo::getPerformanceDataId,
                        FacilityBuyOutSurplusProcessedDataInfo::getFacilityId
                ));
        if(!facilitiesProcessed.isEmpty()) {
            addBuyOutProcessedFacilitiesToBeCertified(facilitiesProcessed, facilitiesEligibleForCertification);
        }

        // Facilities with no performance data get status from UNA
        Set<Long> facilitiesUnprocessed = SetUtils.difference(facilityIds, new HashSet<>(facilitiesProcessed.values()));
        if(!facilitiesUnprocessed.isEmpty()) {
            addBuyOutUnprocessedFacilitiesToBeCertified(payload, facilitiesUnprocessed, facilitiesEligibleForCertification);
        }

        return facilitiesEligibleForCertification;
    }

    @Override
    public PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.FACILITY;
    }

    private void addBuyOutProcessedFacilitiesToBeCertified(Map<Long, Long> facilitiesProcessed, Set<Long> facilitiesEligibleForCertification) {
        // Find facilities transactions
        Map<Long, List<BuyOutSurplusTransactionDetailInfoDTO>> transactionDetailsMap = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionDetailsByPerformanceDataIds(facilitiesProcessed.keySet()).stream()
                .collect(Collectors.groupingBy(BuyOutSurplusTransactionDetailInfoDTO::getPerformanceDataId));

        transactionDetailsMap.forEach((performanceDataId, transactionDetails) -> {
            // If it has transaction as not AWAITING_PAYMENT then certify facilities
            if(!transactionDetails.stream().allMatch(tr -> tr.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.TERMINATED))
                    && transactionDetails.stream().noneMatch(tr -> tr.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT))) {
                facilitiesEligibleForCertification.add(facilitiesProcessed.get(performanceDataId));
            }
        });

        // Certify facilities with no transaction details
        SetUtils.difference(facilitiesProcessed.keySet(), transactionDetailsMap.keySet())
                .forEach(pd -> facilitiesEligibleForCertification.add(facilitiesProcessed.get(pd)));
    }

    private void addBuyOutUnprocessedFacilitiesToBeCertified(final FacilityCertificationAccountProcessingRequestPayload payload,
                                                   Set<Long> facilityIds, Set<Long> facilitiesEligibleForCertification) {
        final TargetPeriodInfoDTO targetPeriod = payload.getTargetPeriod();
        final CertificationPeriodDTO certificationPeriod = payload.getCertificationPeriodDetails();

        // Get facilities from UNA
        List<Facility> newEntrants = payload.getUnderlyingAgreement().getUnderlyingAgreement().getFacilities().stream()
                .filter(f -> f.getFacilityItem().getFacilityDetails().getApplicationReason() == ApplicationReasonType.NEW_AGREEMENT)
                .toList();

        // Get facilities data info
        payload.getFacilities().stream().filter(f -> facilityIds.contains(f.getId())).forEach(facility -> {
            LocalDate activationDate = facility.getCreatedDate().toLocalDate();

            if(activationDate.isAfter(targetPeriod.getEndDate()) && activationDate.isBefore(certificationPeriod.getStartDate())
                    && isNewEntrantFacility(newEntrants, facility)) {
                facilitiesEligibleForCertification.add(facility.getId());
            }
        });
    }

    private boolean isNewEntrantFacility(List<Facility> facilities, FacilityDTO facility) {
        return facilities.stream().anyMatch(f -> f.getFacilityItem().getFacilityId().equals(facility.getFacilityBusinessId()));
    }
}
