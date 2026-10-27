package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.rules.services.authorityinfo.providers.BuyOutSurplusTransactionInfoProvider;
import uk.gov.cca.api.common.domain.HistoryEntity;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionReceivedAmountHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionHistoryMapper;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionQueryService implements BuyOutSurplusTransactionInfoProvider {

    private final BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;
    private final FileEvidenceService fileEvidenceService;
    private static final BuyOutSurplusTransactionHistoryMapper HISTORY_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionHistoryMapper.class);

    @Override
    public Long getAccountIdByBuyOutSurplusTransactionId(Long transactionId) {
        return buyOutSurplusTransactionRepository.findAccountIdByTransactionId(transactionId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<BuyOutSurplusTransactionReceivedAmountHistoryDTO> getBuyOutSurplusTransactionReceivedAmountHistory(Long transactionId) {

        // clean up the unused evidence files
        fileEvidenceService.cleanUpUnusedFilesAsync();

        BuyOutSurplusTransaction transaction = buyOutSurplusTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return transaction.getTransactionHistoryList().stream()
                .filter(hs -> hs.getChangeType() == BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .sorted(Comparator.comparing(HistoryEntity::getSubmissionDate).reversed())
                .map(HISTORY_MAPPER::toBuyOutSurplusTransactionReceivedAmountHistoryDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Set<UUID> getBuyOutSurplusTransactionHistoryEvidenceFiles(Long transactionId) {
        BuyOutSurplusTransaction transaction = buyOutSurplusTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

        return transaction.getTransactionHistoryList().stream()
                .map(history -> history.getPayload().getEvidenceFiles())
                .map(Map::keySet)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
