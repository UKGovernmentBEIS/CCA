package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.BuyOutSurplusTransactionDetailInfoProvider;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailsSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionHistoryMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionDetailQueryService implements BuyOutSurplusTransactionDetailInfoProvider {

    private final BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;
    private static final BuyOutSurplusTransactionMapper MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);
    private static final BuyOutSurplusTransactionHistoryMapper HISTORY_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionHistoryMapper.class);

    public FacilityBuyOutSurplusTransactionDetailSearchResultsDTO getBuyOutSurplusTransactionDetailsList(
            Long transactionId, FacilityBuyOutSurplusTransactionDetailsSearchCriteria criteria) {

        final String term = StringUtils.isBlank(criteria.getTerm()) ? "" : criteria.getTerm().toLowerCase().trim();
        final Pageable pageable = PageRequest.of(
                criteria.getPaging().getPageNumber(),
                criteria.getPaging().getPageSize(),
                Sort.by("fd.facilityBusinessId")
        );

        Page<FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO> results = buyOutSurplusTransactionDetailRepository
                .findBySearchCriteria(pageable, transactionId, term, criteria.getPaymentStatus());

        if (ObjectUtils.isEmpty(results)) {
            return FacilityBuyOutSurplusTransactionDetailSearchResultsDTO.emptyResults();
        }

        return FacilityBuyOutSurplusTransactionDetailSearchResultsDTO.builder()
                .transactionDetails(results.stream().toList())
                .total(results.getTotalElements())
                .build();
    }

    public List<BuyOutSurplusTransactionDetailInfoDTO> getBuyOutSurplusTransactionDetailsByIds(Set<Long> ids) {
        return buyOutSurplusTransactionDetailRepository.findAllByIdIn(ids).stream()
                .map(MAPPER::toBuyOutSurplusTransactionDetailInfoDTO)
                .toList();
    }

    public Optional<BuyOutSurplusTransactionDetailInfoDTO> getBuyOutSurplusTransactionDetailByPerformanceData(Long performanceDataId, PerformanceDataResourceType resourceType) {
        return buyOutSurplusTransactionDetailRepository.findByPerformanceDataIdAndTransaction_PerformanceDataResourceType(performanceDataId, resourceType)
                .map(MAPPER::toBuyOutSurplusTransactionDetailInfoDTO);
    }

    public List<BuyOutSurplusTransactionDetailInfoDTO> getBuyOutSurplusTransactionDetailsByPerformanceDataIds(Set<Long> performanceDataIds, PerformanceDataResourceType resourceType) {
        return buyOutSurplusTransactionDetailRepository.findAllByPerformanceDataIdInAndTransaction_PerformanceDataResourceType(performanceDataIds, resourceType).stream()
                .map(MAPPER::toBuyOutSurplusTransactionDetailInfoDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BuyOutSurplusTransactionDetailHistoryDTO> getBuyOutSurplusTransactionDetailHistory(Long id) {
        List<BuyOutSurplusTransactionDetailHistory> transactionHistoryList =
                buyOutSurplusTransactionDetailRepository.findById(id)
                        .map(BuyOutSurplusTransactionDetail::getTransactionHistoryList)
                        .orElse(Collections.emptyList());

        return transactionHistoryList.stream()
                .map(HISTORY_MAPPER::toBuyOutSurplusTransactionDetailHistoryDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getAccountIdByBuyOutSurplusTransactionDetailId(Long transactionDetailId) {
        return buyOutSurplusTransactionDetailRepository.findById(transactionDetailId)
                .map(detail -> detail.getTransaction().getAccountId())
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
