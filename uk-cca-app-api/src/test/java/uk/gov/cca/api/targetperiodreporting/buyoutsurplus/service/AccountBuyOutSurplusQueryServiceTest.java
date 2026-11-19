package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.PerformanceDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountBuyOutSurplusQueryServiceTest {

    @InjectMocks
    private AccountBuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;

    @Mock
    private BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private BuyOutSurplusEligibleAccountsCustomRepository buyOutSurplusEligibleAccountsCustomRepository;

    @Mock
    private BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;

    @Mock
    private FileDocumentStorageService fileDocumentStorageService;

    @Mock
    private PerformanceDataQueryService performanceDataQueryService;

    @Test
    void getEligibleAccountsForBuyOutSurplusByTargetPeriod() {
        final long accountId = 999L;
        final String accountName = "name";
        final String accountBusinessId= "businessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final TargetPeriodInfoDTO targetPeriodInfoDTO = TargetPeriodInfoDTO.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .businessId(targetPeriodType)
                .build();

        final List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(TargetUnitAccountBusinessInfoDTO.builder()
                .accountId(accountId).businessId(accountBusinessId).name(accountName).build());

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodInfoDTO);
        when(buyOutSurplusEligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType))
                .thenReturn(eligibleAccounts);
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of());

        // Invoke
        final List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService.getEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType);

        // Verify
        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(buyOutSurplusEligibleAccountsCustomRepository, times(1))
                .findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
        verify(buyOutSurplusExclusionRepository, times(1))
                .findAllAccountIds();
        assertThat(results.getFirst()).isEqualTo(eligibleAccounts.getFirst());
    }

    @Test
    void getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod() {
        final long accountId = 999L;
        final String accountName = "name";
        final String accountBusinessId = "businessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final TargetPeriodInfoDTO targetPeriodInfoDTO = TargetPeriodInfoDTO.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .businessId(targetPeriodType)
                .build();
        final TargetUnitAccountBusinessInfoDTO eligibleAccount =
                TargetUnitAccountBusinessInfoDTO.builder()
                        .accountId(accountId)
                        .businessId(accountBusinessId)
                        .name(accountName)
                        .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodInfoDTO);
        when(buyOutSurplusEligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of(accountId));

        // Invoke
        List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService
                .getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType);

        // Verify
        verify(targetPeriodService, times(1))
                .getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(buyOutSurplusEligibleAccountsCustomRepository).findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
        verify(buyOutSurplusExclusionRepository).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }

    @Test
    void getBuyOutSurplusTransactionDetailByPerformanceData() {
        final Long performanceDataId = 1L;
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.ACCOUNT;

        final BuyOutSurplusTransactionDetail transaction = BuyOutSurplusTransactionDetail.builder()
                .id(11L)
                .performanceDataId(11L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();
        final BuyOutSurplusTransactionDetailInfoDTO expected = BuyOutSurplusTransactionDetailInfoDTO.builder()
                .id(11L)
                .performanceDataId(11L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findByPerformanceDataIdAndTransaction_PerformanceDataResourceType(performanceDataId, resourceType))
                .thenReturn(Optional.of(transaction));

        // Invoke
        Optional<BuyOutSurplusTransactionDetailInfoDTO> result = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId);

        // Verify
        assertThat(result).isPresent().contains(expected);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findByPerformanceDataIdAndTransaction_PerformanceDataResourceType(performanceDataId, resourceType);
    }

    @Test
    void getBuyOutSurplusTransactionsInfo() {
        final long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
                .id(1L)
                .transactionDetailList(List.of(
                        BuyOutSurplusTransactionDetail.builder().id(22L).buyOutFee(BigDecimal.TEN).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
                ))
                .performanceDataResourceType(PerformanceDataResourceType.ACCOUNT)
                .build();
        final BuyOutSurplusTransactionDetailInfoDTO expected = BuyOutSurplusTransactionDetailInfoDTO.builder()
                .id(22L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(TargetPeriodInfoDTO.builder().schemeVersion(SchemeVersion.CCA_2).build());
        when(buyOutSurplusTransactionRepository
                .findAllByAccountIdAndTargetPeriodType(accountId, targetPeriodType))
                .thenReturn(List.of(entity));

        // Invoke
        List<BuyOutSurplusTransactionDetailInfoDTO> results = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionsInfo(accountId, targetPeriodType);

        // Verify
        assertThat(results.getFirst()).isEqualTo(expected);
        verify(targetPeriodService, times(1))
                .getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findAllByAccountIdAndTargetPeriodType(accountId, targetPeriodType);
    }

    @Test
    void getBuyOutSurplusTransactionsInfo_erroneous_TP_version() {
        final long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(TargetPeriodInfoDTO.builder().schemeVersion(SchemeVersion.CCA_3).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> buyOutSurplusQueryService.getBuyOutSurplusTransactionsInfo(accountId, targetPeriodType));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(targetPeriodService, times(1))
                .getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verifyNoInteractions(buyOutSurplusTransactionRepository);
    }

    @Test
    void getBuyOutSurplusTransactionDetailsByPerformanceDataIds() {
        final Set<Long> performanceDataIds = Set.of(1L);
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.ACCOUNT;

        final BuyOutSurplusTransactionDetail transaction = BuyOutSurplusTransactionDetail.builder()
                .id(1L)
                .performanceDataId(11L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();
        final BuyOutSurplusTransactionDetailInfoDTO expected = BuyOutSurplusTransactionDetailInfoDTO.builder()
                .id(1L)
                .performanceDataId(11L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findAllByPerformanceDataIdInAndTransaction_PerformanceDataResourceType(performanceDataIds, resourceType))
                .thenReturn(List.of(transaction));

        // Invoke
        List<BuyOutSurplusTransactionDetailInfoDTO> result = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionDetailsByPerformanceDataIds(performanceDataIds);

        // Verify
        assertThat(result).contains(expected);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findAllByPerformanceDataIdInAndTransaction_PerformanceDataResourceType(performanceDataIds, resourceType);
    }

    @Test
    void getBuyOutSurplusTransaction() {
        final Long id = 1L;

        BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
                .id(id)
                .invoicedPaymentDeadline(LocalDate.of(2025, 1, 1))
                .transactionCode("TC00001")
                .creationDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .chargeType(BuyOutSurplusChargeType.FEE)
                .fileDocumentUuid("uuid")
                .transactionDetailList(List.of(BuyOutSurplusTransactionDetail.builder()
                        .id(22L)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .priBuyOutCost(BigDecimal.valueOf(10))
                                .invoicedBuyOutFee(BigDecimal.valueOf(20))
                                .invoicedSurplusGained(BigDecimal.valueOf(30))
                                .invoicedPreviousPaidFees(BigDecimal.valueOf(40))
                                .build())
                        .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                        .buyOutFee(BigDecimal.valueOf(50))
                        .performanceDataId(11L)
                        .build()))
                .build();
        PerformanceDataDetailsInfoDTO performanceDataDTO = PerformanceDataDetailsInfoDTO.builder()
                .accountBusinessId("ACC-123")
                .operatorName("Test Operator")
                .reportVersion(1)
                .targetPeriodResultType(TargetPeriodResultType.TARGET_MET)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .priBuyOutCarbon(BigDecimal.valueOf(100))
                .build();
        FileInfoDTO file = FileInfoDTO.builder()
                .name("filename")
                .uuid("someUuid")
                .build();

        AccountBuyOutSurplusTransactionDTO dto = AccountBuyOutSurplusTransactionDTO.builder()
                .id(id)
                .detailId(22L)
                .transactionCode("TC00001")
                .accountBusinessId("ACC-123")
                .operatorName("Test Operator")
                .targetPeriodResultType(TargetPeriodResultType.TARGET_MET)
                .reportVersion("1")
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .fileInfoDTO(file)
                .creationDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .dueDate(LocalDate.of(2025, 1, 1))
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .priBuyOutCarbon(BigDecimal.valueOf(100))
                .priBuyOutCost(BigDecimal.valueOf(10))
                .invoicedBuyOutFee(BigDecimal.valueOf(20))
                .invoicedSurplusGained(BigDecimal.valueOf(30))
                .invoicedPreviousPaidFees(BigDecimal.valueOf(40))
                .buyOutFee(BigDecimal.valueOf(50))
                .build();

        when(buyOutSurplusTransactionRepository
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.ACCOUNT))
                .thenReturn(Optional.of(entity));
        when(performanceDataQueryService.getPerformanceDataBuyOutSurplusTransactionDetails(11L))
                .thenReturn(performanceDataDTO);
        when(fileDocumentStorageService.getFileInfoDTO("uuid")).thenReturn(file);

        // Invoke
        final AccountBuyOutSurplusTransactionDTO result = buyOutSurplusQueryService.getBuyOutSurplusTransaction(id);

        // Verify
        assertThat(result).isEqualTo(dto);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.ACCOUNT);
        verify(performanceDataQueryService, times(1))
                .getPerformanceDataBuyOutSurplusTransactionDetails(11L);
        verify(fileDocumentStorageService, times(1))
                .getFileInfoDTO("uuid");
    }

    @Test
    void getResourceType() {
        assertThat(buyOutSurplusQueryService.getResourceType()).isEqualTo(PerformanceDataResourceType.ACCOUNT);
    }
}
