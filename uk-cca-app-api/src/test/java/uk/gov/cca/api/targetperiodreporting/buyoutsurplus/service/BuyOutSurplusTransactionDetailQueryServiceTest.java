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

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailsSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.common.domain.PagingRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionDetailQueryServiceTest {

    @InjectMocks
    private BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Mock
    private BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;

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
        FacilityBuyOutSurplusTransactionDetailSearchResultsDTO result = buyOutSurplusTransactionDetailQueryService
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
        FacilityBuyOutSurplusTransactionDetailSearchResultsDTO result = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailsList(transactionId, criteria);

        // Verify
        assertThat(result).isEqualTo(expectedResults);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findBySearchCriteria(pageable, transactionId, "", null);
    }

    @Test
    void getBuyOutSurplusTransactionDetailsByIds() {
        final Set<Long> ids = Set.of(1L, 2L);

        final List<BuyOutSurplusTransactionDetail> transactionDetails = List.of(
                BuyOutSurplusTransactionDetail.builder().id(1L).performanceDataId(11L).buyOutFee(BigDecimal.ONE).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetail.builder().id(2L).performanceDataId(22L).buyOutFee(BigDecimal.TWO).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );

        when(buyOutSurplusTransactionDetailRepository.findAllByIdIn(ids)).thenReturn(transactionDetails);

        // Invoke
        List<BuyOutSurplusTransactionDetailInfoDTO> results = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailsByIds(ids);

        // Verify
        assertThat(results).containsExactlyInAnyOrderElementsOf(List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).performanceDataId(11L).buyOutFee(BigDecimal.ONE).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).performanceDataId(22L).buyOutFee(BigDecimal.TWO).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        ));
        verify(buyOutSurplusTransactionDetailRepository, times(1)).findAllByIdIn(ids);
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
        Optional<BuyOutSurplusTransactionDetailInfoDTO> result = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailByPerformanceData(performanceDataId, resourceType);

        // Verify
        assertThat(result).isPresent().contains(expected);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findByPerformanceDataIdAndTransaction_PerformanceDataResourceType(performanceDataId, resourceType);
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
        List<BuyOutSurplusTransactionDetailInfoDTO> result = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailsByPerformanceDataIds(performanceDataIds, resourceType);

        // Verify
        assertThat(result).contains(expected);
        verify(buyOutSurplusTransactionDetailRepository, times(1))
                .findAllByPerformanceDataIdInAndTransaction_PerformanceDataResourceType(performanceDataIds, resourceType);
    }

    @Test
    void getBuyOutSurplusTransactionDetailHistory() {
        final Long id = 1L;

        final BuyOutSurplusTransactionDetailAmountChangedHistoryPayload payload = BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.builder()
                .type(BuyOutSurplusTransactionDetailChangeType.AMOUNT_CHANGED)
                .amount(BigDecimal.ONE)
                .comments("My comments")
                .evidenceFiles(Map.of(UUID.randomUUID(), "evidence"))
                .build();
        final BuyOutSurplusTransactionDetailHistory history = BuyOutSurplusTransactionDetailHistory.builder()
                .id(22L)
                .changeType(BuyOutSurplusTransactionDetailChangeType.AMOUNT_CHANGED)
                .payload(payload)
                .submitterId("submitterId")
                .submitter("submitter")
                .submissionDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .build();
        final BuyOutSurplusTransactionDetail detail = BuyOutSurplusTransactionDetail.builder()
                .transactionHistoryList(List.of(history))
                .build();

        final BuyOutSurplusTransactionDetailHistoryDTO historyDTO = BuyOutSurplusTransactionDetailHistoryDTO.builder()
                .id(22L)
                .submitter("submitter")
                .submissionDate(LocalDate.of(2024, 1, 1).atStartOfDay())
                .payload(payload)
                .build();

        when(buyOutSurplusTransactionDetailRepository.findById(id))
                .thenReturn(Optional.of(detail));

        // Invoke
        List<BuyOutSurplusTransactionDetailHistoryDTO> result = buyOutSurplusTransactionDetailQueryService
                .getBuyOutSurplusTransactionDetailHistory(id);

        // Verify
        assertThat(result).containsExactly(historyDTO);
    }
}
