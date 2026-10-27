package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityBuyOutSurplusProcessedDataInfo;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;

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
    private BuyOutSurplusProcessedDataQueryService buyOutSurplusProcessedDataQueryService;

    @Mock
    private BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Test
    void getEligibleAccounts() {
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
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
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .targetPeriodType(targetPeriodType)
                .build();
        final List<Long> facilityIds = List.of(1L, 2L, 3L, 4L, 5L);
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .accountId(100L)
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

        when(buyOutSurplusProcessedDataQueryService.getFacilitiesBuyOutSurplusProcessedData(new HashSet<>(facilityIds), targetPeriodType))
                .thenReturn(facilitiesProcessed);
        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailsByPerformanceDataIds(Set.of(11L, 22L, 33L), PerformanceDataResourceType.FACILITY))
                .thenReturn(transactionDetails);

        // Invoke
        Set<Long> result = facilityCertificationRunFacilityService
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);

        // Verify
        assertThat(result).containsExactlyInAnyOrderElementsOf(Set.of(1L, 3L));
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getFacilitiesBuyOutSurplusProcessedData(new HashSet<>(facilityIds), targetPeriodType);
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailsByPerformanceDataIds(Set.of(11L, 22L, 33L), PerformanceDataResourceType.FACILITY);
    }

    @Test
    void getResourceType() {
        assertThat(facilityCertificationRunFacilityService.getResourceType())
                .isEqualTo(PerformanceDataResourceType.FACILITY);
    }
}
