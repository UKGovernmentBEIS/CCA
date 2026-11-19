package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.BuyOutSurplusTransactionDetailInfoProvider;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionHistoryMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionDetailQueryService implements BuyOutSurplusTransactionDetailInfoProvider {

    private final BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;
    private static final BuyOutSurplusTransactionMapper MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);
    private static final BuyOutSurplusTransactionHistoryMapper HISTORY_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionHistoryMapper.class);

    public List<BuyOutSurplusTransactionDetailInfoDTO> getBuyOutSurplusTransactionDetailsByIds(Set<Long> ids) {
        return buyOutSurplusTransactionDetailRepository.findAllByIdIn(ids).stream()
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
