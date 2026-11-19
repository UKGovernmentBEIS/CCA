package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilityBuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestPayload;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunFacilityServiceTest {

    @InjectMocks
    private FacilityCertificationRunFacilityService facilityCertificationRunFacilityService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private FacilityBuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;

    @Mock
    private FacilityBuyOutSurplusQueryService buyOutSurplusQueryService;

    @Test
    void getEligibleAccounts() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .targetPeriodType(targetPeriodType)
                .build();

        final List<TargetUnitAccountBusinessInfoDTO> accounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(1L).businessId("accountId1").build(),
                TargetUnitAccountBusinessInfoDTO.builder().accountId(2L).businessId("accountId2").build()
        );

        final Map<Long, FacilityCertificationAccountState> accountStates = Map.of(
                1L, FacilityCertificationAccountState.builder().accountId(1L).accountBusinessId("accountId1").build(),
                2L, FacilityCertificationAccountState.builder().accountId(2L).accountBusinessId("accountId2").build()
        );

        when(targetUnitAccountQueryService.getActiveAccounts()).thenReturn(accounts);

        // Invoke
        Map<Long, FacilityCertificationAccountState> result = facilityCertificationRunFacilityService
                .getEligibleAccounts(certificationPeriod);

        // Verify
        assertThat(result).containsExactlyInAnyOrderEntriesOf(accountStates);
        verify(targetUnitAccountQueryService, times(1)).getActiveAccounts();
    }

    @Test
    void getFacilitiesEligibleForCertification() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final FacilityCertificationAccountProcessingRequestPayload payload = FacilityCertificationAccountProcessingRequestPayload.builder()
                .targetPeriod(TargetPeriodInfoDTO.builder().businessId(targetPeriodType).endDate(LocalDate.of(2026, 12, 31)).build())
                .certificationPeriodDetails(CertificationPeriodDTO.builder().startDate(LocalDate.of(2027, 7, 1)).build())
                .facilities(List.of(
                        FacilityDTO.builder().id(4L).facilityBusinessId("4").createdDate(LocalDate.of(2027, 4, 4).atStartOfDay()).build(),
                        FacilityDTO.builder().id(5L).facilityBusinessId("5").createdDate(LocalDate.of(2027, 5, 5).atStartOfDay()).build(),
                        FacilityDTO.builder().id(6L).facilityBusinessId("6").createdDate(LocalDate.of(2026, 12, 31).atStartOfDay()).build(),
                        FacilityDTO.builder().id(7L).facilityBusinessId("7").createdDate(LocalDate.of(2027, 7, 1).atStartOfDay()).build()
                ))
                .underlyingAgreement(UnderlyingAgreementContainer.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("4")
                                                        .facilityDetails(FacilityDetails.builder().applicationReason(ApplicationReasonType.NEW_AGREEMENT).build())
                                                        .build())
                                                .build(),
                                        Facility.builder()
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityId("5")
                                                        .facilityDetails(FacilityDetails.builder().applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP).build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();
        final List<Long> facilityIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);
        final Long accountId = 100L;
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(accountId)
                .facilityIds(facilityIds)
                .build();

        final List<FacilityBuyOutSurplusProcessedDataInfo> facilitiesProcessed = List.of(
                FacilityBuyOutSurplusProcessedDataInfo.builder().facilityId(1L).performanceDataId(11L).build(),
                FacilityBuyOutSurplusProcessedDataInfo.builder().facilityId(2L).performanceDataId(22L).build(),
                FacilityBuyOutSurplusProcessedDataInfo.builder().facilityId(3L).performanceDataId(33L).build()
        );
        final List<BuyOutSurplusTransactionDetailInfoDTO> transactionDetails = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().performanceDataId(11L).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().performanceDataId(22L).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build()
        );

        when(buyOutSurplusProcessedDataService.getLatestBuyOutSurplusProcessedPrimaryPerformanceData(new HashSet<>(facilityIds), targetPeriodType))
                .thenReturn(facilitiesProcessed);
        when(buyOutSurplusQueryService.getBuyOutSurplusTransactionDetailsByPerformanceDataIds(Set.of(11L, 22L, 33L)))
                .thenReturn(transactionDetails);

        // Invoke
        Set<Long> result = facilityCertificationRunFacilityService
                .getFacilitiesEligibleForCertification(payload, accountState);

        // Verify
        assertThat(result).containsExactlyInAnyOrderElementsOf(Set.of(1L, 3L, 4L));
        verify(buyOutSurplusProcessedDataService, times(1))
                .getLatestBuyOutSurplusProcessedPrimaryPerformanceData(new HashSet<>(facilityIds), targetPeriodType);
        verify(buyOutSurplusQueryService, times(1))
                .getBuyOutSurplusTransactionDetailsByPerformanceDataIds(Set.of(11L, 22L, 33L));
    }

    @Test
    void getResourceType() {
        assertThat(facilityCertificationRunFacilityService.getResourceType())
                .isEqualTo(PerformanceDataResourceType.FACILITY);
    }
}
