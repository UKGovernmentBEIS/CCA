package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationRunAccountServiceTest {

    @InjectMocks
    private FacilityCertificationRunAccountService facilityCertificationRunAccountService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

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

        final List<AccountPerformanceDataInfo> accounts = List.of(
                AccountPerformanceDataInfo.builder().accountId(1L).accountBusinessId("accountId1").lastPerformanceDataId(11L).build(),
                AccountPerformanceDataInfo.builder().accountId(2L).accountBusinessId("accountId2").lastPerformanceDataId(22L).build()
        );

        final Map<Long, FacilityCertificationAccountState> accountStates = Map.of(
                1L, FacilityCertificationAccountState.builder().accountId(1L).accountBusinessId("accountId1").lastPerformanceDataId(11L).build(),
                2L, FacilityCertificationAccountState.builder().accountId(2L).accountBusinessId("accountId2").lastPerformanceDataId(22L).build()
        );

        when(accountPerformanceDataStatusQueryService.findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType))
                .thenReturn(accounts);

        // Invoke
        Map<Long, FacilityCertificationAccountState> result = facilityCertificationRunAccountService
                .getEligibleAccounts(certificationPeriod);

        // Verify
        assertThat(result).containsExactlyInAnyOrderEntriesOf(accountStates);
        verify(accountPerformanceDataStatusQueryService, times(1))
                .findAccountsWithPerformanceDataForTargetPeriod(targetPeriodType);
    }

    @Test
    void getFacilitiesEligibleForCertification() {
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        when(buyOutSurplusProcessedDataQueryService.getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(1L));
        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId, PerformanceDataResourceType.ACCOUNT))
                .thenReturn(Optional.empty());

        // Invoke
        Set<Long> result = facilityCertificationRunAccountService
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);

        // Verify
        assertThat(result).containsExactlyInAnyOrderElementsOf(facilityIds);
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId, PerformanceDataResourceType.ACCOUNT);
    }

    @Test
    void getFacilitiesEligibleForCertification_with_paid_payment() {
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        final BuyOutSurplusTransactionDetailInfoDTO info = BuyOutSurplusTransactionDetailInfoDTO.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(buyOutSurplusProcessedDataQueryService.getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(1L));
        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId, PerformanceDataResourceType.ACCOUNT))
                .thenReturn(Optional.of(info));

        // Invoke
        Set<Long> result = facilityCertificationRunAccountService
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);

        // Verify
        assertThat(result).containsExactlyInAnyOrderElementsOf(facilityIds);
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId, PerformanceDataResourceType.ACCOUNT);
    }

    @Test
    void getFacilitiesEligibleForCertification_with_await_payment() {
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        final BuyOutSurplusTransactionDetailInfoDTO info = BuyOutSurplusTransactionDetailInfoDTO.builder()
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .build();

        when(buyOutSurplusProcessedDataQueryService.getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.of(1L));
        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId, PerformanceDataResourceType.ACCOUNT))
                .thenReturn(Optional.of(info));

        // Invoke
        Set<Long> result = facilityCertificationRunAccountService
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);

        // Verify
        assertThat(result).isEmpty();
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId, PerformanceDataResourceType.ACCOUNT);
    }

    @Test
    void getFacilitiesEligibleForCertification_no_buyout_surplus() {
        final List<Long> facilityIds = List.of(1L, 2L, 3L);
        final long performanceDataId = 10L;
        final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2025, 7, 1))
                .build();
        final FacilityCertificationAccountState accountState = FacilityCertificationAccountState.builder()
                .lastPerformanceDataId(performanceDataId)
                .facilityIds(facilityIds)
                .build();

        when(buyOutSurplusProcessedDataQueryService.getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId))
                .thenReturn(Optional.empty());

        // Invoke
        Set<Long> result = facilityCertificationRunAccountService
                .getFacilitiesEligibleForCertification(certificationPeriod, accountState);

        // Verify
        assertThat(result).isEmpty();
        verify(buyOutSurplusProcessedDataQueryService, times(1))
                .getAccountBuyOutSurplusProcessedDataByPerformanceData(performanceDataId);
        verifyNoInteractions(buyOutSurplusTransactionDetailQueryService);
    }

    @Test
    void getResourceType() {
        assertThat(facilityCertificationRunAccountService.getResourceType())
                .isEqualTo(PerformanceDataResourceType.ACCOUNT);
    }
}
