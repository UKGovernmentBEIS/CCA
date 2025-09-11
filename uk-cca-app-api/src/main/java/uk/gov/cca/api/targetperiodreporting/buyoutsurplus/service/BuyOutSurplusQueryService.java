package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionSummaryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionDetailsMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionHistoryMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.PerformanceDataQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusQueryService {

    private final BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;
    private final BuyOutSurplusTransactionCustomRepository buyOutSurplusTransactionCustomRepository;
    private final BuyOutSurplusEligibleAccountsCustomRepository eligibleAccountsCustomRepository;
    private final BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;
    private final PerformanceDataQueryService performanceDataQueryService;
    private final FileDocumentService fileDocumentService;
    private static final BuyOutSurplusTransactionMapper BUY_OUT_SURPLUS_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);
    private static final BuyOutSurplusTransactionDetailsMapper BUY_OUT_SURPLUS_DETAILS_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionDetailsMapper.class);
    private static final BuyOutSurplusTransactionHistoryMapper BUY_OUT_SURPLUS_HISTORY_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionHistoryMapper.class);
    
    public List<BuyOutSurplusTransactionInfoDTO> getAllTransactionInfoByAccountAndTargetPeriodPessimistic(Long accountId, TargetPeriodType targetPeriodType) {
        return buyOutSurplusTransactionRepository
                .findAllByAccountIdAndTargetPeriodTypePessimistic(accountId, targetPeriodType).stream()
                .map(BUY_OUT_SURPLUS_MAPPER::toBuyOutSurplusTransactionInfoDTO)
                .toList();
    }

    public List<BuyOutSurplusTransactionInfoDTO> getAllTransactionInfoByIds(Set<Long> ids) {
        return buyOutSurplusTransactionRepository.findAllByIdIn(ids).stream()
                .map(BUY_OUT_SURPLUS_MAPPER::toBuyOutSurplusTransactionInfoDTO)
                .toList();
    }

    public List<TargetUnitAccountBusinessInfoDTO> getAllEligibleAccountsByTargetPeriod(TargetPeriodType targetPeriodType) {
        final List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = eligibleAccountsCustomRepository
                .findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);

        final List<Long> excluded = buyOutSurplusExclusionRepository.findAllAccountIds();

        return eligibleAccounts.stream()
                .filter(eligibleAccountDTO -> !excluded.contains(eligibleAccountDTO.getAccountId()))
                .toList();
    }

    public List<TargetUnitAccountBusinessInfoDTO> getAllExcludedEligibleAccountsByTargetPeriod(TargetPeriodType targetPeriodType) {
        final List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = eligibleAccountsCustomRepository
                .findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);

        final List<Long> excluded = buyOutSurplusExclusionRepository.findAllAccountIds();

        return eligibleAccounts.stream()
                .filter(eligibleAccountDTO -> excluded.contains(eligibleAccountDTO.getAccountId()))
                .sorted(Comparator.comparing(TargetUnitAccountBusinessInfoDTO::getBusinessId))
                .toList();
    }
    
    public BuyOutSurplusTransactionsListDTO getBuyOutSurplusTransactionsList(AppUser appUser, BuyOutSurplusTransactionsListSearchCriteria criteria) {
        return buyOutSurplusTransactionCustomRepository.findBuyOutSurplusTransactions(appUser.getCompetentAuthority(), criteria);
    }

    public BuyOutSurplusTransactionDetailsDTO getBuyOutSurplusTransactionDetails(Long id) {
        BuyOutSurplusTransactionDTO buyOutSurplusTransactionDTO =
                BUY_OUT_SURPLUS_MAPPER.toBuyOutSurplusTransactionDTO(
                        buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionById(id)
                                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND)));

        PerformanceDataDetailsInfoDTO performanceDataDetailsInfoDTO =
                performanceDataQueryService.getPerformanceDataBuyOutSurplusTransactionDetails(buyOutSurplusTransactionDTO.getPerformanceDataId());

        FileInfoDTO fileInfoDTO = fileDocumentService.getFileInfoDTO(buyOutSurplusTransactionDTO.getFileDocumentUuid());

        return BUY_OUT_SURPLUS_DETAILS_MAPPER.toBuyOutSurplusTransactionDetailsDTO(buyOutSurplusTransactionDTO, performanceDataDetailsInfoDTO, fileInfoDTO);
    }

    public boolean existsBuyOutSurplusTransactionByIdAndDocumentId(Long id, String documentUuid) {
        return buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionByIdAndFileDocumentUuid(id, documentUuid).isPresent();
    }
    
    @Transactional(readOnly = true)
    public List<BuyOutSurplusTransactionHistoryDTO> getBuyOutSurplusTransactionHistory(Long id) {
        List<BuyOutSurplusTransactionHistory> transactionHistoryList =
                buyOutSurplusTransactionRepository.findById(id)
                        .map(BuyOutSurplusTransaction::getTransactionHistoryList)
                        .orElse(Collections.emptyList());
        
        return transactionHistoryList.stream()
                .map(BUY_OUT_SURPLUS_HISTORY_MAPPER::toBuyOutSurplusTransactionHistoryDTO)
                .toList();
    }

    public BuyOutSurplusTransactionSummaryDTO getBuyOutSurplusTransactionSummary(Long transactionId) {
        return BUY_OUT_SURPLUS_DETAILS_MAPPER
                .toBuyOutSurplusTransactionSummaryDTO(buyOutSurplusTransactionRepository.findById(transactionId)
                        .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND)));
    }

    public Optional<BuyOutSurplusTransactionInfoDTO> getBuyOutSurplusTransactionByPerformanceData(Long performanceDataId) {
        return buyOutSurplusTransactionRepository.findByPerformanceDataId(performanceDataId)
                .map(BUY_OUT_SURPLUS_MAPPER::toBuyOutSurplusTransactionInfoDTO);
    }
}
