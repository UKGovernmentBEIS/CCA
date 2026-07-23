package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AvailableTargetPeriodsBuyOutDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionListItemDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionSummaryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionHistoryMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.PerformanceDataQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusQueryServiceTest {

    @InjectMocks
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;

    @Mock
    private BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;

    @Mock
    private BuyOutSurplusEligibleAccountsCustomRepository eligibleAccountsCustomRepository;
    
    @Mock
    private BuyOutSurplusTransactionCustomRepository buyOutSurplusTransactionCustomRepository;
    
    @Mock
    private PerformanceDataQueryService performanceDataQueryService;
    
    @Mock
    private FileDocumentService fileDocumentService;
    
    @Mock
    private BuyOutSurplusTransactionHistoryMapper buyOutSurplusHistoryMapper;
    
    @Mock
    private TargetPeriodService targetPeriodService;
    
    @Mock
    private SchemeTerminationHelper schemeTerminationHelper;

    @Test
    void getAllTransactionInfoByAccountAndTargetPeriodPessimistic() {
        final long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
                .id(1L)
                .transactionCode("CCA060020")
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .fileDocumentUuid("fileDocumentUuid")
                .build();
        final BuyOutSurplusTransactionInfoDTO expected = BuyOutSurplusTransactionInfoDTO.builder()
                .id(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(buyOutSurplusTransactionRepository
                .findAllByAccountIdAndTargetPeriodTypePessimistic(accountId, targetPeriodType))
                .thenReturn(List.of(entity));

        // Invoke
        List<BuyOutSurplusTransactionInfoDTO> results = buyOutSurplusQueryService.getAllTransactionInfoByAccountAndTargetPeriodPessimistic(accountId, targetPeriodType);

        // Verify
        assertThat(results.getFirst()).isEqualTo(expected);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findAllByAccountIdAndTargetPeriodTypePessimistic(accountId, targetPeriodType);
    }

    @Test
    void getAllTransactionInfoByIds() {
        final Set<Long> ids = Set.of(1L);

        final List<BuyOutSurplusTransaction> transactions = List.of(
                BuyOutSurplusTransaction.builder().id(1L).buyOutFee(BigDecimal.TEN).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
        );
        final BuyOutSurplusTransactionInfoDTO expected = BuyOutSurplusTransactionInfoDTO.builder()
                .id(1L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(buyOutSurplusTransactionRepository.findAllByIdIn(ids)).thenReturn(transactions);

        // Invoke
        List<BuyOutSurplusTransactionInfoDTO> results = buyOutSurplusQueryService.getAllTransactionInfoByIds(ids);

        // Verify
        assertThat(results.getFirst()).isEqualTo(expected);
        verify(buyOutSurplusTransactionRepository, times(1)).findAllByIdIn(ids);
    }

    @Test
    void getAllEligibleAccountsByTargetPeriod() {
        final long accountId = 999L;
        final String accountName = "name";
        final String accountBusinessId= "businessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(TargetUnitAccountBusinessInfoDTO.builder()
                .accountId(accountId).businessId(accountBusinessId).name(accountName).build());

        when(eligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType))
                .thenReturn(eligibleAccounts);

        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of());

        final List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService.getAllEligibleAccountsByTargetPeriod(targetPeriodType);


        verify(eligibleAccountsCustomRepository, times(1))
                .findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
        verify(buyOutSurplusExclusionRepository, times(1))
                .findAllAccountIds();
        assertThat(results.getFirst()).isEqualTo(eligibleAccounts.getFirst());
    }

    @Test
    void getAllExcludedEligibleAccountsByTargetPeriod() {

        final long accountId = 999L;
        final String accountName = "name";
        final String accountBusinessId= "businessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

        final List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = List.of(TargetUnitAccountBusinessInfoDTO.builder()
                .accountId(accountId).businessId(accountBusinessId).name(accountName).build());

        when(eligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType))
                .thenReturn(eligibleAccounts);

        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of(eligibleAccounts.getFirst().getAccountId()));

        final List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService.getAllExcludedEligibleAccountsByTargetPeriod(targetPeriodType);


        verify(eligibleAccountsCustomRepository, times(1))
                .findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
        verify(buyOutSurplusExclusionRepository, times(1))
                .findAllAccountIds();
        assertThat(results.getFirst()).isEqualTo(eligibleAccounts.getFirst());

    }
    
    @Test
    void getBuyOutSurplusTransactionsList_usingSearchCriteria() {
        BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .term("some term")
                .targetPeriodType(TargetPeriodType.TP6)
                .paging(PagingRequest.builder()
                        .pageNumber(1)
                        .pageSize(25)
                        .build())
                .build();
        
        BuyOutSurplusTransactionListItemDTO item = BuyOutSurplusTransactionListItemDTO.builder()
                .transactionCode("some term")
                .build();
        
        BuyOutSurplusTransactionsListDTO dto = BuyOutSurplusTransactionsListDTO.builder()
                .transactions(List.of(item))
                .total(1L)
                .build();
        
        AppUser appUser = mock(AppUser.class);
        
        when(buyOutSurplusTransactionCustomRepository.findBuyOutSurplusTransactions(appUser.getCompetentAuthority(), criteria)).thenReturn(dto);
        
        final BuyOutSurplusTransactionsListDTO resultDto = buyOutSurplusQueryService.getBuyOutSurplusTransactionsList(appUser, criteria);
    
        verify(buyOutSurplusTransactionCustomRepository, times(1)).findBuyOutSurplusTransactions(appUser.getCompetentAuthority(), criteria);
        assertThat(resultDto).isEqualTo(dto);
    }
    
    @Test
    void getBuyOutSurplusTransactionsDetails_byTransactionId() {
        
        BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
                .id(1L)
                .transactionCode("TC00001")
                .performanceDataId(1L)
                .creationDate(LocalDateTime.now())
                .fileDocumentUuid("uuid")
                .build();
        
        PerformanceDataDetailsInfoDTO performanceDataDetailsInfoDTO =
                PerformanceDataDetailsInfoDTO.builder()
                        .build();
        
        FileInfoDTO fileInfoDTO = FileInfoDTO.builder().build();
        
        when(fileDocumentService.getFileInfoDTO("uuid")).thenReturn(fileInfoDTO);
        when(buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionById(1L)).thenReturn(Optional.of(entity));
        when(performanceDataQueryService.getPerformanceDataBuyOutSurplusTransactionDetails(1L)).thenReturn(performanceDataDetailsInfoDTO);
        
        final BuyOutSurplusTransactionDetailsDTO resultDto = buyOutSurplusQueryService.getBuyOutSurplusTransactionDetails(1L);
        
        verify(buyOutSurplusTransactionRepository, times(1)).findBuyOutSurplusTransactionById(1L);
        assertEquals(resultDto.getTransactionCode(), entity.getTransactionCode());
        assertEquals(resultDto.getCreationDate(), entity.getCreationDate());
    }
    
    @Test
    void existsBuyOutSurplusTransactionByIdAndDocumentId_ShouldCallRepositoryWithCorrectParameters() {
        Long transactionId = 123L;
        UUID documentUuid = UUID.randomUUID();
        String documentUuidString = documentUuid.toString();
        
        buyOutSurplusQueryService.existsBuyOutSurplusTransactionByIdAndDocumentId(transactionId, documentUuidString);
        
        verify(buyOutSurplusTransactionRepository)
                .findBuyOutSurplusTransactionByIdAndFileDocumentUuid(transactionId, documentUuidString);
        verifyNoMoreInteractions(buyOutSurplusTransactionRepository);
    }
    
    @Test
    void getBuyOutSurplusTransactionHistory_shouldReturnEmptyListWhenNoHistoryFound() {
        Long transactionId = 1L;
        BuyOutSurplusTransaction transaction = new BuyOutSurplusTransaction();
        when(buyOutSurplusTransactionRepository
                .findById(transactionId))
                .thenReturn(Optional.of(transaction));
        
        List<BuyOutSurplusTransactionHistoryDTO> result =
                buyOutSurplusQueryService.getBuyOutSurplusTransactionHistory(transactionId);
        
        assertTrue(result.isEmpty());
        verify(buyOutSurplusTransactionRepository)
                .findById(transactionId);
        verifyNoInteractions(buyOutSurplusHistoryMapper);
    }
    
    @Test
    void getBuyOutSurplusTransactionHistory_shouldReturnResultsWhenHistoryFound() {
        Long transactionId = 1L;
        BuyOutSurplusTransaction transaction = new BuyOutSurplusTransaction();
        LocalDateTime now = LocalDateTime.now();
        
        BuyOutSurplusTransactionHistory newerHistory = createTestHistory(2L, "User1");
        newerHistory.setSubmissionDate(now.minusDays(1));
        
        BuyOutSurplusTransactionHistory olderHistory = createTestHistory(1L, "User2");
        olderHistory.setSubmissionDate(now);
        
        transaction.setTransactionHistoryList(List.of(newerHistory, olderHistory));
        
        when(buyOutSurplusTransactionRepository
                .findById(transactionId))
                .thenReturn(Optional.of(transaction));
        
        List<BuyOutSurplusTransactionHistoryDTO> result =
                buyOutSurplusQueryService.getBuyOutSurplusTransactionHistory(transactionId);
        
        assertEquals(2, result.size());
        verify(buyOutSurplusTransactionRepository)
                .findById(transactionId);
    }
    
    private BuyOutSurplusTransactionHistory createTestHistory(Long id, String submitter) {
        return BuyOutSurplusTransactionHistory.builder()
                .id(id)
                .submitter(submitter)
                .submissionDate(LocalDateTime.now())
                .build();
    }

    @Test
    void getBuyOutSurplusTransactionSummary() {
        final Long transactionId = 1L;

        final BuyOutSurplusTransaction transactionEntity = BuyOutSurplusTransaction.builder()
                .id(transactionId)
                .transactionCode("TRX12345")
                .build();

        final BuyOutSurplusTransactionSummaryDTO expectedDTO = BuyOutSurplusTransactionSummaryDTO.builder()
                .transactionCode("TRX12345")
                .build();

        when(buyOutSurplusTransactionRepository.findById(transactionId))
                .thenReturn(Optional.of(transactionEntity));

        BuyOutSurplusTransactionSummaryDTO result =
                buyOutSurplusQueryService.getBuyOutSurplusTransactionSummary(transactionId);

        verify(buyOutSurplusTransactionRepository, times(1)).findById(transactionId);
        assertThat(result).isEqualTo(expectedDTO);
    }

    @Test
    void getBuyOutSurplusTransactionByPerformanceData() {
        final Long performanceDataId = 1L;

        final BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .id(11L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();
        final BuyOutSurplusTransactionInfoDTO expected = BuyOutSurplusTransactionInfoDTO.builder()
                .id(11L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(buyOutSurplusTransactionRepository.findByPerformanceDataId(performanceDataId))
                .thenReturn(Optional.of(transaction));

        // Invoke
        Optional<BuyOutSurplusTransactionInfoDTO> result = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionByPerformanceData(performanceDataId);

        // Verify
        assertThat(result).isPresent().contains(expected);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findByPerformanceDataId(performanceDataId);
    }
    
    @Test
    void getAvailableBuyOutTargetPeriods() {
        LocalDate date = LocalDate.of(2026, 1, 1);

        TargetPeriod tp6 = TargetPeriod.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP6)
                .schemeVersion(SchemeVersion.CCA_2)
                .buyOutStartDate(LocalDate.of(2023, 1, 1))
                .build();

        TargetPeriod cca3Current = TargetPeriod.builder()
                .id(2L)
                .schemeVersion(SchemeVersion.CCA_3)
                .buyOutStartDate(LocalDate.of(2025, 1, 1))
                .build();

        TargetPeriod cca3Previous = TargetPeriod.builder()
                .id(3L)
                .schemeVersion(SchemeVersion.CCA_3)
                .buyOutStartDate(LocalDate.of(2024, 1, 1))
                .build();

        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(date))
                .thenReturn(List.of(tp6, cca3Current, cca3Previous));
        when(schemeTerminationHelper.isAfterCca2TerminationDate(date)).thenReturn(false);

        AvailableTargetPeriodsBuyOutDTO result = buyOutSurplusQueryService.getAvailableBuyOutTargetPeriods(date);

        assertThat(result.getCurrentTargetPeriods())
                .containsExactly(
                        TargetPeriodBuyOutDetailsDTO.builder().id(1L).businessId(TargetPeriodType.TP6).build(),
                        TargetPeriodBuyOutDetailsDTO.builder().id(2L).build());
        assertThat(result.getPreviousTargetPeriods())
                .containsExactly(
                        TargetPeriodBuyOutDetailsDTO.builder().id(3L).build());
        verify(targetPeriodService).getTargetPeriodBuyOutCurrentAndPrevious(date);
        verify(schemeTerminationHelper).isAfterCca2TerminationDate(date);
    }
    
    @Test
    void getAvailableBuyOutTargetPeriods_cca2NotActive_singleCca3Period() {
        LocalDate date = LocalDate.of(2026, 1, 1);

        TargetPeriod tp6 = TargetPeriod.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP6)
                .schemeVersion(SchemeVersion.CCA_2)
                .buyOutStartDate(LocalDate.of(2024, 1, 1))
                .build();

        TargetPeriod cca3 = TargetPeriod.builder()
                .id(2L)
                .schemeVersion(SchemeVersion.CCA_3)
                .buyOutStartDate(LocalDate.of(2025, 1, 1))
                .build();

        when(targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(date))
                .thenReturn(List.of(tp6, cca3));
        when(schemeTerminationHelper.isAfterCca2TerminationDate(date)).thenReturn(true);

        AvailableTargetPeriodsBuyOutDTO result = buyOutSurplusQueryService.getAvailableBuyOutTargetPeriods(date);

        assertThat(result.getCurrentTargetPeriods())
                .containsExactly(
                        TargetPeriodBuyOutDetailsDTO.builder()
                                .id(2L)
                                .build());
        assertThat(result.getPreviousTargetPeriods()).isEmpty();
        verify(targetPeriodService).getTargetPeriodBuyOutCurrentAndPrevious(date);
        verify(schemeTerminationHelper).isAfterCca2TerminationDate(date);
    }
}
