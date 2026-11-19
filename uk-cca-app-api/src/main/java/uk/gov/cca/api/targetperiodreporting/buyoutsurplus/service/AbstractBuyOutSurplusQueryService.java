package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractBuyOutSurplusQueryService {

    private final BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;
    protected final BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository;
    protected final TargetPeriodService targetPeriodService;
    protected final BuyOutSurplusEligibleAccountsCustomRepository buyOutSurplusEligibleAccountsCustomRepository;
    protected final BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;
    protected final FileDocumentStorageService fileDocumentStorageService;

    private static final BuyOutSurplusTransactionMapper BUY_OUT_SURPLUS_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);

    public List<TargetUnitAccountBusinessInfoDTO> getEligibleAccountsForBuyOutSurplusByTargetPeriod(TargetPeriodType targetPeriodType) {
        validateTargetPeriodType(targetPeriodType);

        List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = this.findEligibleAccountsForPerformanceData(targetPeriodType);

        final List<Long> excluded = buyOutSurplusExclusionRepository.findAllAccountIds();

        return eligibleAccounts.stream()
                .filter(eligibleAccountDTO -> !excluded.contains(eligibleAccountDTO.getAccountId()))
                .toList();
    }

    public List<TargetUnitAccountBusinessInfoDTO> getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(TargetPeriodType targetPeriodType) {
        validateTargetPeriodType(targetPeriodType);

        List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts = this.findEligibleAccountsForPerformanceData(targetPeriodType);

        final List<Long> excluded = buyOutSurplusExclusionRepository.findAllAccountIds();

        return eligibleAccounts.stream()
                .filter(eligibleAccountDTO -> excluded.contains(eligibleAccountDTO.getAccountId()))
                .sorted(Comparator.comparing(TargetUnitAccountBusinessInfoDTO::getBusinessId))
                .toList();
    }

    public Optional<BuyOutSurplusTransactionDetailInfoDTO> getBuyOutSurplusTransactionDetailByPerformanceData(Long performanceDataId) {
        return buyOutSurplusTransactionDetailRepository
                .findByPerformanceDataIdAndTransaction_PerformanceDataResourceType(performanceDataId, this.getResourceType())
                .map(BUY_OUT_SURPLUS_MAPPER::toBuyOutSurplusTransactionDetailInfoDTO);
    }

    public List<BuyOutSurplusTransactionDetailInfoDTO> getBuyOutSurplusTransactionsInfo(Long accountId, TargetPeriodType targetPeriodType) {
        validateTargetPeriodType(targetPeriodType);

        return buyOutSurplusTransactionRepository
                .findAllByAccountIdAndTargetPeriodType(accountId, targetPeriodType).stream()
                .map(entity -> BUY_OUT_SURPLUS_MAPPER.toBuyOutSurplusTransactionDetailInfoDTO(entity.getTransactionDetailList().getFirst()))
                .toList();
    }

    public List<BuyOutSurplusTransactionDetailInfoDTO> getBuyOutSurplusTransactionDetailsByPerformanceDataIds(Set<Long> performanceDataIds) {
        return buyOutSurplusTransactionDetailRepository
                .findAllByPerformanceDataIdInAndTransaction_PerformanceDataResourceType(performanceDataIds, this.getResourceType()).stream()
                .map(BUY_OUT_SURPLUS_MAPPER::toBuyOutSurplusTransactionDetailInfoDTO)
                .toList();
    }

    protected abstract List<TargetUnitAccountBusinessInfoDTO> findEligibleAccountsForPerformanceData(TargetPeriodType targetPeriodType);

    protected abstract PerformanceDataResourceType getResourceType();

    private void validateTargetPeriodType(TargetPeriodType targetPeriodType) {
        SchemeVersion schemeVersion = targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType).getSchemeVersion();
        if(schemeVersion.getResourceType() != this.getResourceType()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
