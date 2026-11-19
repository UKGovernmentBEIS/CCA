package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailsSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityBuyOutSurplusQueryServiceTest {

    @InjectMocks
    private FacilityBuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;

    @Mock
    protected TargetPeriodService targetPeriodService;

    @Mock
    protected BuyOutSurplusEligibleAccountsCustomRepository buyOutSurplusEligibleAccountsCustomRepository;

    @Mock
    private BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;

    @Mock
    private FileDocumentStorageService fileDocumentStorageService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;

    @Test
    void getEligibleAccountsForBuyOutSurplusByTargetPeriod() {
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
        when(buyOutSurplusEligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
                Set.of(targetPeriodType)))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of());

        // Invoke
        final List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService.getEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType);

        // Verify
        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService, times(1)).findByTargetPeriodType(targetPeriodType);
        verify(buyOutSurplusEligibleAccountsCustomRepository, times(1))
                .findAccountsWithFacilityPerformanceDataPendingBuyOut(Set.of(targetPeriodType));
        verify(buyOutSurplusExclusionRepository, times(1)).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }

    @Test
    void getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod() {
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
        when(buyOutSurplusEligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
                Set.of(targetPeriodType)))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds())
                .thenReturn(List.of(accountId));

        // Invoke
        List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService
                .getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType);

        // Verify
        verify(targetPeriodService, times(1)).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);
        verify(buyOutSurplusEligibleAccountsCustomRepository).findAccountsWithFacilityPerformanceDataPendingBuyOut(Set.of(targetPeriodType));
        verify(buyOutSurplusExclusionRepository).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }

    @Test
    void getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod_afterSecondaryReportingStartDate() {
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
        when(buyOutSurplusEligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(
                applicableTargetPeriods))
                .thenReturn(List.of(eligibleAccount));
        when(buyOutSurplusExclusionRepository.findAllAccountIds()).thenReturn(List.of(accountId));

        // Invoke
        List<TargetUnitAccountBusinessInfoDTO> results = buyOutSurplusQueryService
                .getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType);

        // Verify
        verify(targetPeriodService).getTargetPeriodInfoByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService).findByTargetPeriodType(targetPeriodType);
        verify(targetPeriodService).getTargetPeriodsInfoForSchemeWithStartDateUpTo(
                SchemeVersion.CCA_3,
                LocalDate.of(2026, 1, 1));
        verify(buyOutSurplusEligibleAccountsCustomRepository)
                .findAccountsWithFacilityPerformanceDataPendingBuyOut(applicableTargetPeriods);
        verify(buyOutSurplusExclusionRepository).findAllAccountIds();
        assertThat(results).containsExactly(eligibleAccount);
    }

    @Test
    void getTransactionDetailByPerformanceData() {
        final Long performanceDataId = 1L;
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.FACILITY;

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
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        final BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
                .id(1L)
                .transactionDetailList(List.of(
                        BuyOutSurplusTransactionDetail.builder().id(22L).buyOutFee(BigDecimal.TEN).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build()
                ))
                .performanceDataResourceType(PerformanceDataResourceType.FACILITY)
                .build();
        final BuyOutSurplusTransactionDetailInfoDTO expected = BuyOutSurplusTransactionDetailInfoDTO.builder()
                .id(22L)
                .buyOutFee(BigDecimal.TEN)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType))
                .thenReturn(TargetPeriodInfoDTO.builder().schemeVersion(SchemeVersion.CCA_3).build());
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
    void getBuyOutSurplusTransactionDetailsByPerformanceDataIds() {
        final Set<Long> performanceDataIds = Set.of(1L);
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.FACILITY;

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

        final Long accountId = 2L;
        final BuyOutSurplusTransaction entity = BuyOutSurplusTransaction.builder()
                .id(id)
                .accountId(accountId)
                .targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP7).build())
                .invoicedPaymentDeadline(LocalDate.of(2025, 1, 1))
                .invoicedBuyOutFee(BigDecimal.valueOf(200))
                .receivedAmount(BigDecimal.valueOf(60))
                .transactionCode("TC00001")
                .creationDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .chargeType(BuyOutSurplusChargeType.FEE)
                .fileDocumentUuid("uuid")
                .transactionDetailList(List.of(
                        BuyOutSurplusTransactionDetail.builder()
                                .id(22L)
                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                        .priBuyOutCost(BigDecimal.valueOf(10))
                                        .invoicedBuyOutFee(BigDecimal.valueOf(20))
                                        .invoicedSurplusGained(BigDecimal.valueOf(30))
                                        .invoicedPreviousPaidFees(BigDecimal.valueOf(40))
                                        .build())
                                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                                .buyOutFee(BigDecimal.valueOf(50))
                                .build(),
                        BuyOutSurplusTransactionDetail.builder()
                                .id(23L)
                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                        .priBuyOutCost(BigDecimal.valueOf(20))
                                        .invoicedBuyOutFee(BigDecimal.valueOf(30))
                                        .invoicedSurplusGained(BigDecimal.valueOf(40))
                                        .invoicedPreviousPaidFees(BigDecimal.valueOf(50))
                                        .build())
                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                                .buyOutFee(BigDecimal.valueOf(60))
                                .build()
                ))
                .build();
        final TargetUnitAccountBusinessInfoDTO account = TargetUnitAccountBusinessInfoDTO.builder()
                .accountId(accountId)
                .businessId("accountBusinessId")
                .name("accountName")
                .build();
        final FileInfoDTO file = FileInfoDTO.builder()
                .name("filename")
                .uuid("someUuid")
                .build();

        final FacilityBuyOutSurplusTransactionDTO dto = FacilityBuyOutSurplusTransactionDTO.builder()
                .id(id)
                .targetPeriodType(TargetPeriodType.TP7)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .transactionCode("TC00001")
                .accountBusinessId("accountBusinessId")
                .operatorName("accountName")
                .fileInfoDTO(file)
                .invoicedBuyOutFee(BigDecimal.valueOf(200))
                .creationDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .dueDate(LocalDate.of(2025, 1, 1))
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .expectedAmount(BigDecimal.valueOf(110))
                .receivedAmount(BigDecimal.valueOf(60))
                .amountAllocated(BigDecimal.valueOf(50))
                .expectedMarkedFacilities(2)
                .actualMarkedFacilities(1)
                .hasBulkAction(true)
                .build();

        when(buyOutSurplusTransactionRepository
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.FACILITY))
                .thenReturn(Optional.of(entity));
        when(targetUnitAccountQueryService.getTargetUnitAccountBusinessInfoDTO(accountId)).thenReturn(account);
        when(fileDocumentStorageService.getFileInfoDTO("uuid")).thenReturn(file);

        // Invoke
        final FacilityBuyOutSurplusTransactionDTO result = buyOutSurplusQueryService.getBuyOutSurplusTransaction(id);

        // Verify
        assertThat(result).isEqualTo(dto);
        verify(buyOutSurplusTransactionRepository, times(1))
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.FACILITY);
        verify(targetUnitAccountQueryService, times(1))
                .getTargetUnitAccountBusinessInfoDTO(accountId);
        verify(fileDocumentStorageService, times(1))
                .getFileInfoDTO("uuid");
    }

    @Test
    void getBuyOutSurplusTransactionDetailsList() {
        final Long transactionId = 1L;
        final FacilityBuyOutSurplusTransactionDetailsSearchCriteria criteria = FacilityBuyOutSurplusTransactionDetailsSearchCriteria.builder()
                .term("facility")
                .paging(PagingRequest.builder().pageNumber(0).pageSize(50).build())
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        final Pageable pageable = PageRequest.of(0, 50, Sort.by("fd.facilityBusinessId"));
        final FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO resultInfoDto = FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO.builder()
                .buyOutFee(BigDecimal.ZERO)
                .build();
        final Page<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO> pageResult = new PageImpl<>(List.of(resultInfoDto));

        final FacilityBuyOutSurplusTransactionDetailSearchResultsDTO expectedResults = FacilityBuyOutSurplusTransactionDetailSearchResultsDTO.builder()
                .transactionDetails(List.of(resultInfoDto))
                .total(1L)
                .build();

        when(buyOutSurplusTransactionDetailRepository
                .findBySearchCriteria(pageable, transactionId, "facility", BuyOutSurplusPaymentStatus.PAID))
                .thenReturn(pageResult);

        // Invoke
        FacilityBuyOutSurplusTransactionDetailSearchResultsDTO result = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionDetailsList(transactionId, criteria);

        // Verify
        assertThat(result).isEqualTo(expectedResults);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findBySearchCriteria(pageable, transactionId, "facility", BuyOutSurplusPaymentStatus.PAID);
    }

    @Test
    void getBuyOutSurplusTransactionDetailsList_empty_results() {
        final Long transactionId = 1L;
        final FacilityBuyOutSurplusTransactionDetailsSearchCriteria criteria = FacilityBuyOutSurplusTransactionDetailsSearchCriteria.builder()
                .paging(PagingRequest.builder().pageNumber(0).pageSize(50).build())
                .build();

        final Pageable pageable = PageRequest.of(0, 50, Sort.by("fd.facilityBusinessId"));
        final Page<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO> pageResult = new PageImpl<>(List.of());

        final FacilityBuyOutSurplusTransactionDetailSearchResultsDTO expectedResults = FacilityBuyOutSurplusTransactionDetailSearchResultsDTO.builder()
                .transactionDetails(List.of())
                .total(0L)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findBySearchCriteria(pageable, transactionId, "", null))
                .thenReturn(pageResult);

        // Invoke
        FacilityBuyOutSurplusTransactionDetailSearchResultsDTO result = buyOutSurplusQueryService
                .getBuyOutSurplusTransactionDetailsList(transactionId, criteria);

        // Verify
        assertThat(result).isEqualTo(expectedResults);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findBySearchCriteria(pageable, transactionId, "", null);
    }

    @Test
    void getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = List.of(1L, 2L);

        when(buyOutSurplusTransactionRepository.findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds))
                .thenReturn(List.of());

        // Invoke
        buyOutSurplusQueryService.getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds);

        // Verify
        verify(buyOutSurplusTransactionRepository).findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds);
    }

    @Test
    void getResourceType() {
        assertThat(buyOutSurplusQueryService.getResourceType()).isEqualTo(PerformanceDataResourceType.FACILITY);
    }
}
