package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.PerformanceDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

import java.util.List;

@Service
public class AccountBuyOutSurplusQueryService extends AbstractBuyOutSurplusQueryService implements BuyOutSurplusQueryService {

    private final PerformanceDataQueryService performanceDataQueryService;
    private static final BuyOutSurplusTransactionMapper BUY_OUT_SURPLUS_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);

    public AccountBuyOutSurplusQueryService(
            BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository,
            BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository,
            TargetPeriodService targetPeriodService,
            BuyOutSurplusEligibleAccountsCustomRepository buyOutSurplusEligibleAccountsCustomRepository,
            BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository,
            FileDocumentStorageService fileDocumentStorageService,
            PerformanceDataQueryService performanceDataQueryService) {

        super(buyOutSurplusExclusionRepository, buyOutSurplusTransactionDetailRepository, targetPeriodService,
                buyOutSurplusEligibleAccountsCustomRepository, buyOutSurplusTransactionRepository, fileDocumentStorageService);
        this.performanceDataQueryService = performanceDataQueryService;
    }

    @Override
    protected List<TargetUnitAccountBusinessInfoDTO> findEligibleAccountsForPerformanceData(TargetPeriodType targetPeriodType) {
        return buyOutSurplusEligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
    }

    @Transactional(readOnly = true)
    public AccountBuyOutSurplusTransactionDTO getBuyOutSurplusTransaction(Long id) {
        BuyOutSurplusTransaction transaction = buyOutSurplusTransactionRepository
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, this.getResourceType())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        BuyOutSurplusTransactionDetail transactionDetail = transaction.getTransactionDetailList().getFirst();

        PerformanceDataDetailsInfoDTO performanceDataDetailsInfoDTO = performanceDataQueryService
                .getPerformanceDataBuyOutSurplusTransactionDetails(transactionDetail.getPerformanceDataId());

        FileInfoDTO fileInfoDTO = fileDocumentStorageService.getFileInfoDTO(transaction.getFileDocumentUuid());

        return BUY_OUT_SURPLUS_MAPPER
                .toAccountBuyOutSurplusTransactionDTO(transaction, transactionDetail, performanceDataDetailsInfoDTO, fileInfoDTO);
    }

    @Override
    public PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.ACCOUNT;
    }
}
