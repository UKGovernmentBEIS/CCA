package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AvailableTargetPeriodsBuyOutDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionListItemDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleFacilitiesCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.PerformanceDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private FileDocumentStorageService fileDocumentStorageService;
    
    @Mock
    private TargetPeriodService targetPeriodService;
    
    @Mock
    private SchemeTerminationHelper schemeTerminationHelper;
    
    @Mock
    private BuyOutSurplusEligibleFacilitiesCustomRepository buyOutSurplusEligibleFacilitiesCustomRepository;

    @Test
    void getAccountTransactionInfo() {
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

        when(buyOutSurplusTransactionRepository
                .findAllByAccountIdAndTargetPeriodType(accountId, targetPeriodType))
                .thenReturn(List.of(entity));

        // Invoke
        List<BuyOutSurplusTransactionDetailInfoDTO> results = buyOutSurplusQueryService
                .getAccountTransactionInfo(accountId, targetPeriodType);

        // Verify
        assertThat(results.getFirst()).isEqualTo(expected);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findAllByAccountIdAndTargetPeriodType(accountId, targetPeriodType);
    }

    @Test
    void getAllEligibleAccountsByTargetPeriod() {
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
        when(eligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType))
                .thenReturn(eligibleAccounts);
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of());

        final List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService.getAllEligibleAccountsByTargetPeriod(targetPeriodType);

        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(eligibleAccountsCustomRepository, times(1))
                .findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
        verify(buyOutSurplusExclusionRepository, times(1))
                .findAllAccountIds();
        assertThat(results.getFirst()).isEqualTo(eligibleAccounts.getFirst());
    }
    
    @Test
    void getAllEligibleAccountsByTargetPeriod_facilityPerformanceData() {
        final long accountId = 999L;
        final String accountName = "name";
        final String accountBusinessId = "businessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final TargetPeriodInfoDTO targetPeriodInfoDTO = TargetPeriodInfoDTO.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(targetPeriodType)
                .build();

        final TargetUnitAccountBusinessInfoDTO eligibleAccount =
                TargetUnitAccountBusinessInfoDTO.builder()
                        .accountId(accountId)
                        .businessId(accountBusinessId)
                        .name(accountName)
                        .build();
        final TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(targetPeriodType)
                .schemeVersion(SchemeVersion.CCA_3)
                .startDate(LocalDate.of(2026, 1, 1))
                .secondaryReportingStartDate(LocalDate.now().plusDays(10))
                .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodInfoDTO);
        when(targetPeriodService.findByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(eligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
                Set.of(targetPeriodType)))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of());

        final List<TargetUnitAccountBusinessInfoDTO> results =
                buyOutSurplusQueryService.getAllEligibleAccountsByTargetPeriod(targetPeriodType);

        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService, times(1)).findByTargetPeriodType(targetPeriodType);
        verify(eligibleAccountsCustomRepository, times(1))
                .findAccountsWithFacilityPerformanceDataPendingBuyOut(Set.of(targetPeriodType));
        verify(buyOutSurplusExclusionRepository, times(1)).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }

    @Test
    void getAllExcludedEligibleAccountsByTargetPeriod_accountPerformanceData() {
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
        when(eligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of(accountId));

        List<TargetUnitAccountBusinessInfoDTO> results =
                buyOutSurplusQueryService.getAllExcludedEligibleAccountsByTargetPeriod(targetPeriodType);

        verify(targetPeriodService, times(1))
                .getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(eligibleAccountsCustomRepository).findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
        verify(buyOutSurplusExclusionRepository).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }
    
    @Test
    void getAllExcludedEligibleAccountsByTargetPeriod_facilityPerformanceData() {
        final long accountId = 999L;
        final String accountBusinessId = "businessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final TargetPeriodInfoDTO targetPeriodInfoDTO = TargetPeriodInfoDTO.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(targetPeriodType)
                .build();
        final TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(targetPeriodType)
                .schemeVersion(SchemeVersion.CCA_3)
                .startDate(LocalDate.of(2026, 1, 1))
                .secondaryReportingStartDate(LocalDate.of(2100, 1, 1))
                .build();

        final TargetUnitAccountBusinessInfoDTO eligibleAccount =
                TargetUnitAccountBusinessInfoDTO.builder()
                        .accountId(accountId)
                        .businessId(accountBusinessId)
                        .name("name")
                        .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodInfoDTO);
        when(targetPeriodService.findByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);
        when(eligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
                Set.of(targetPeriodType)))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of(accountId));

        List<TargetUnitAccountBusinessInfoDTO> results =
                buyOutSurplusQueryService.getAllExcludedEligibleAccountsByTargetPeriod(targetPeriodType);

        verify(targetPeriodService, times(1)).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);
        verify(eligibleAccountsCustomRepository).findAccountsWithFacilityPerformanceDataPendingBuyOut(Set.of(targetPeriodType));
        verify(buyOutSurplusExclusionRepository).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }
    
    @Test
    void getAllExcludedEligibleAccountsByTargetPeriod_facilityPerformanceData_afterSecondaryReportingStartDate() {
        final long accountId = 999L;
        final String accountBusinessId = "businessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP8;
        final TargetPeriodInfoDTO targetPeriodInfoDTO = TargetPeriodInfoDTO.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .businessId(targetPeriodType)
                .build();
        final TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(targetPeriodType)
                .schemeVersion(SchemeVersion.CCA_3)
                .startDate(LocalDate.of(2026, 1, 1))
                .secondaryReportingStartDate(LocalDate.of(2025, 1, 1))
                .build();
        final TargetUnitAccountBusinessInfoDTO eligibleAccount =
                TargetUnitAccountBusinessInfoDTO.builder()
                        .accountId(accountId)
                        .businessId(accountBusinessId)
                        .name("name")
                        .build();

        final List<TargetPeriodInfoDTO> applicableTargetPeriodsInfo = List.of(
                TargetPeriodInfoDTO.builder()
                        .businessId(TargetPeriodType.TP7)
                        .build(),
                TargetPeriodInfoDTO.builder()
                        .businessId(TargetPeriodType.TP8)
                        .build());

        final Set<TargetPeriodType> applicableTargetPeriods =
                Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8);

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriodInfoDTO);

        when(targetPeriodService.findByTargetPeriodType(targetPeriodType))
                .thenReturn(targetPeriod);

        when(targetPeriodService.getTargetPeriodsInfoForSchemeWithStartDateUpTo(
                SchemeVersion.CCA_3,
                LocalDate.of(2026, 1, 1)))
                .thenReturn(applicableTargetPeriodsInfo);

        when(eligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
                applicableTargetPeriods))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds()).thenReturn(List.of(accountId));

        List<TargetUnitAccountBusinessInfoDTO> results =
                buyOutSurplusQueryService.getAllExcludedEligibleAccountsByTargetPeriod(targetPeriodType);

        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService).getTargetPeriodsInfoForSchemeWithStartDateUpTo(
                SchemeVersion.CCA_3,
                LocalDate.of(2026, 1, 1));
        verify(eligibleAccountsCustomRepository)
                .findAccountsWithFacilityPerformanceDataPendingBuyOut(applicableTargetPeriods);
        verify(buyOutSurplusExclusionRepository).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }
    
    @Test
    void getBuyOutSurplusTransactionsList() {
        BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .term("some term")
                .targetPeriodType(TargetPeriodType.TP6)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        BuyOutSurplusTransactionsListDTO dto = BuyOutSurplusTransactionsListDTO.builder()
                .transactions(List.of(BuyOutSurplusTransactionListItemDTO.builder()
                        .transactionCode("some term")
                        .build()))
                .total(1L)
                .build();
        
        AppUser appUser = mock(AppUser.class);
        
        when(buyOutSurplusTransactionCustomRepository.findBuyOutSurplusTransactions(appUser.getCompetentAuthority(), criteria))
                .thenReturn(dto);

        // Invoke
        final BuyOutSurplusTransactionsListDTO result = buyOutSurplusQueryService.getBuyOutSurplusTransactionsList(appUser, criteria);

        // Verify
        verify(buyOutSurplusTransactionCustomRepository, times(1))
                .findBuyOutSurplusTransactions(appUser.getCompetentAuthority(), criteria);
        assertThat(result).isEqualTo(dto);
    }
    
    @Test
    void getAccountBuyOutSurplusTransaction() {
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
        final AccountBuyOutSurplusTransactionDTO result = buyOutSurplusQueryService.getAccountBuyOutSurplusTransaction(id);

        // Verify
        assertThat(result).isEqualTo(dto);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.ACCOUNT);
        verify(performanceDataQueryService, times(1))
                .getPerformanceDataBuyOutSurplusTransactionDetails(11L);
        verify(fileDocumentStorageService, times(1))
                .getFileInfoDTO("uuid");
    }

  //TODO Restore this once we implement file generation for the buyout workflow.
//    @Test
//    void getFacilityBuyOutSurplusTransaction() {
//        final Long id = 1L;
//
//        final Long accountId = 2L;
//        final BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
//                .id(id)
//                .accountId(accountId)
//                .targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP7).build())
//                .invoicedPaymentDeadline(LocalDate.of(2025, 1, 1))
//                .invoicedBuyOutFee(BigDecimal.valueOf(200))
//                .receivedAmount(BigDecimal.valueOf(60))
//                .transactionCode("TC00001")
//                .creationDate(LocalDate.of(2024, 1, 1).atStartOfDay())
//                .chargeType(BuyOutSurplusChargeType.FEE)
//                .fileDocumentUuid("uuid")
//                .transactionDetailList(List.of(
//                        BuyOutSurplusTransactionDetail.builder()
//                                .id(22L)
//                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
//                                        .priBuyOutCost(BigDecimal.valueOf(10))
//                                        .invoicedBuyOutFee(BigDecimal.valueOf(20))
//                                        .invoicedSurplusGained(BigDecimal.valueOf(30))
//                                        .invoicedPreviousPaidFees(BigDecimal.valueOf(40))
//                                        .build())
//                                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
//                                .buyOutFee(BigDecimal.valueOf(50))
//                                .build(),
//                        BuyOutSurplusTransactionDetail.builder()
//                                .id(23L)
//                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
//                                        .priBuyOutCost(BigDecimal.valueOf(20))
//                                        .invoicedBuyOutFee(BigDecimal.valueOf(30))
//                                        .invoicedSurplusGained(BigDecimal.valueOf(40))
//                                        .invoicedPreviousPaidFees(BigDecimal.valueOf(50))
//                                        .build())
//                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
//                                .buyOutFee(BigDecimal.valueOf(60))
//                                .build()
//                ))
//                .build();
//        final TargetUnitAccountBusinessInfoDTO account = TargetUnitAccountBusinessInfoDTO.builder()
//                .accountId(accountId)
//                .businessId("accountBusinessId")
//                .name("accountName")
//                .build();
//        final FileInfoDTO file = FileInfoDTO.builder()
//                .name("filename")
//                .uuid("someUuid")
//                .build();
//
//        final FacilityBuyOutSurplusTransactionDTO dto = FacilityBuyOutSurplusTransactionDTO.builder()
//                .id(id)
//                .targetPeriodType(TargetPeriodType.TP7)
//                .chargeType(BuyOutSurplusChargeType.FEE)
//                .transactionCode("TC00001")
//                .accountBusinessId("accountBusinessId")
//                .operatorName("accountName")
//                .fileInfoDTO(file)
//                .invoicedBuyOutFee(BigDecimal.valueOf(200))
//                .creationDate(LocalDate.of(2024, 1, 1).atStartOfDay())
//                .dueDate(LocalDate.of(2025, 1, 1))
//                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
//                .expectedAmount(BigDecimal.valueOf(110))
//                .receivedAmount(BigDecimal.valueOf(60))
//                .amountAllocated(BigDecimal.valueOf(50))
//                .expectedMarkedFacilities(2)
//                .actualMarkedFacilities(1)
//                .hasBulkAction(true)
//                .build();
//
//        when(buyOutSurplusTransactionRepository
//                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.FACILITY))
//                .thenReturn(Optional.of(entity));
//        when(targetUnitAccountQueryService.getTargetUnitAccountBusinessInfoDTO(accountId)).thenReturn(account);
//        when(fileDocumentStorageService.getFileInfoDTO("uuid")).thenReturn(file);
//
//        // Invoke
//        final FacilityBuyOutSurplusTransactionDTO result = buyOutSurplusQueryService.getFacilityBuyOutSurplusTransaction(id);
//
//        // Verify
//        assertThat(result).isEqualTo(dto);
//        verify(buyOutSurplusTransactionRepository, times(1))
//                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.FACILITY);
//        verify(targetUnitAccountQueryService, times(1))
//                .getTargetUnitAccountBusinessInfoDTO(accountId);
//        verify(fileDocumentStorageService, times(1))
//                .getFileInfoDTO("uuid");
//    }
    
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
    
    @Test
    void shouldGetTransactionDetailsSummaryByAccountIdAndTargetPeriodIds() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L);

        when(buyOutSurplusTransactionRepository.findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds)).thenReturn(List.of());

        buyOutSurplusQueryService.getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds);

        verify(buyOutSurplusTransactionRepository).findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds);
    }
    
    @Test
    void shouldGroupPerformanceDataByFacilityAndTargetPeriod() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L, 3L);

        when(buyOutSurplusEligibleFacilitiesCustomRepository
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds))
                .thenReturn(List.of(
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP7),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP8),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP9),
                        mockPerformanceData("FACILITY-2", TargetPeriodType.TP9)
                ));

        Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> result =
                buyOutSurplusQueryService.getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, targetPeriodIds);

        assertThat(result).hasSize(2).containsKeys("FACILITY-1", "FACILITY-2");
        assertThat(result.get("FACILITY-1"))
                .containsKeys(
                        TargetPeriodType.TP7,
                        TargetPeriodType.TP8,
                        TargetPeriodType.TP9
                );

        verify(buyOutSurplusEligibleFacilitiesCustomRepository)
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds);
    }

    @Test
    void shouldReturnEmptyMapWhenRepositoryReturnsNoResults() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L, 3L);

        when(buyOutSurplusEligibleFacilitiesCustomRepository
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds))
                .thenReturn(List.of());

        Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> result =
                buyOutSurplusQueryService
                        .getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, targetPeriodIds);

        assertThat(result).isEmpty();

        verify(buyOutSurplusEligibleFacilitiesCustomRepository)
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds);
    }

    @Test
    void shouldOrderTargetPeriodsByNumber() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L, 3L);

        when(buyOutSurplusEligibleFacilitiesCustomRepository
                .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds))
                .thenReturn(List.of(
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP8),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP7),
                        mockPerformanceData("FACILITY-1", TargetPeriodType.TP9)));

        Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> result =
                buyOutSurplusQueryService
                        .getPerformanceDataWithBuyOutProcessedStatusPerFacility(accountId, targetPeriodIds);

        assertThat(result.get("FACILITY-1").keySet())
                .containsExactly(
                        TargetPeriodType.TP7,
                        TargetPeriodType.TP8,
                        TargetPeriodType.TP9
                );
    }   

    private FacilityPerformanceDataWithBuyOutProcessedStatus mockPerformanceData(
            String facilityBusinessId,
            TargetPeriodType targetPeriodType) {

        return FacilityPerformanceDataWithBuyOutProcessedStatus.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(targetPeriodType)
                .build();
    }
}
