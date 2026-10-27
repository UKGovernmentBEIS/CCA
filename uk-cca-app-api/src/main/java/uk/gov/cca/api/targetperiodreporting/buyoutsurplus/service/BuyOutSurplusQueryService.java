package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AvailableTargetPeriodsBuyOutDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleFacilitiesCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.PerformanceDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.transform.TargetPeriodMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusQueryService {

    private final BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository;
    private final BuyOutSurplusTransactionCustomRepository buyOutSurplusTransactionCustomRepository;
    private final BuyOutSurplusEligibleAccountsCustomRepository eligibleAccountsCustomRepository;
    private final BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository;
    private final PerformanceDataQueryService performanceDataQueryService;
    private final TargetPeriodService targetPeriodService;
    private final FileDocumentStorageService fileDocumentStorageService;
    private final SchemeTerminationHelper schemeTerminationHelper;
    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final BuyOutSurplusEligibleFacilitiesCustomRepository buyOutSurplusEligibleFacilitiesCustomRepository;
    
    private static final BuyOutSurplusTransactionMapper BUY_OUT_SURPLUS_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);
    private static final TargetPeriodMapper TARGET_PERIOD_MAPPER = Mappers.getMapper(TargetPeriodMapper.class);
    
    public List<BuyOutSurplusTransactionDetailInfoDTO> getAccountTransactionInfo(Long accountId, TargetPeriodType targetPeriodType) {
        return buyOutSurplusTransactionRepository
                .findAllByAccountIdAndTargetPeriodType(accountId, targetPeriodType).stream()
                .filter(entity -> entity.getPerformanceDataResourceType().equals(PerformanceDataResourceType.ACCOUNT))
                .map(entity -> BUY_OUT_SURPLUS_MAPPER.toBuyOutSurplusTransactionDetailInfoDTO(entity.getTransactionDetailList().getFirst()))
                .toList();
    }

    public List<TargetUnitAccountBusinessInfoDTO> getAllEligibleAccountsByTargetPeriod(TargetPeriodType targetPeriodType) {
        SchemeVersion schemeVersion = targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType).getSchemeVersion();
    	List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts =
                switch (schemeVersion.getResourceType()) {
                    case ACCOUNT -> findEligibleAccountsForAccountPerformanceData(targetPeriodType);
                    case FACILITY -> findEligibleAccountsForFacilityPerformanceData(targetPeriodType);
                };

        final List<Long> excluded = buyOutSurplusExclusionRepository.findAllAccountIds();

        return eligibleAccounts.stream()
                .filter(eligibleAccountDTO -> !excluded.contains(eligibleAccountDTO.getAccountId()))
                .toList();
    }

    public List<TargetUnitAccountBusinessInfoDTO> getAllExcludedEligibleAccountsByTargetPeriod(TargetPeriodType targetPeriodType) {
        SchemeVersion schemeVersion = targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType).getSchemeVersion();
    	List<TargetUnitAccountBusinessInfoDTO> eligibleAccounts =
                switch (schemeVersion.getResourceType()) {
                    case ACCOUNT -> findEligibleAccountsForAccountPerformanceData(targetPeriodType);
                    case FACILITY -> findEligibleAccountsForFacilityPerformanceData(targetPeriodType);
                };

        final List<Long> excluded = buyOutSurplusExclusionRepository.findAllAccountIds();

        return eligibleAccounts.stream()
                .filter(eligibleAccountDTO -> excluded.contains(eligibleAccountDTO.getAccountId()))
                .sorted(Comparator.comparing(TargetUnitAccountBusinessInfoDTO::getBusinessId))
                .toList();
    }
    
    public BuyOutSurplusTransactionsListDTO getBuyOutSurplusTransactionsList(AppUser appUser, BuyOutSurplusTransactionsListSearchCriteria criteria) {
        return buyOutSurplusTransactionCustomRepository
                .findBuyOutSurplusTransactions(appUser.getCompetentAuthority(), criteria);
    }

    @Transactional(readOnly = true)
    public AccountBuyOutSurplusTransactionDTO getAccountBuyOutSurplusTransaction(Long id) {
        BuyOutSurplusTransaction transaction = buyOutSurplusTransactionRepository
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.ACCOUNT)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        BuyOutSurplusTransactionDetail transactionDetail = transaction.getTransactionDetailList().getFirst();

        PerformanceDataDetailsInfoDTO performanceDataDetailsInfoDTO = performanceDataQueryService
                .getPerformanceDataBuyOutSurplusTransactionDetails(transactionDetail.getPerformanceDataId());

        FileInfoDTO fileInfoDTO = fileDocumentStorageService.getFileInfoDTO(transaction.getFileDocumentUuid());

        return BUY_OUT_SURPLUS_MAPPER
                .toAccountBuyOutSurplusTransactionDTO(transaction, transactionDetail, performanceDataDetailsInfoDTO, fileInfoDTO);
    }

    @Transactional(readOnly = true)
    public FacilityBuyOutSurplusTransactionDTO getFacilityBuyOutSurplusTransaction(Long id) {
        BuyOutSurplusTransaction transaction = buyOutSurplusTransactionRepository
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, PerformanceDataResourceType.FACILITY)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        TargetUnitAccountBusinessInfoDTO account = targetUnitAccountQueryService
                .getTargetUnitAccountBusinessInfoDTO(transaction.getAccountId());
        FileInfoDTO fileInfoDTO = fileDocumentStorageService.getFileInfoDTO(transaction.getFileDocumentUuid());

        return BUY_OUT_SURPLUS_MAPPER
                .toFacilityBuyOutSurplusTransactionDTO(transaction, account, fileInfoDTO);
    }

    public boolean existsBuyOutSurplusTransactionByIdAndDocumentId(Long id, String documentUuid) {
        return buyOutSurplusTransactionRepository.findBuyOutSurplusTransactionByIdAndFileDocumentUuid(id, documentUuid).isPresent();
    }
    
    public AvailableTargetPeriodsBuyOutDTO getAvailableBuyOutTargetPeriods(LocalDate date) {
    	List<TargetPeriodBuyOutDetailsDTO> currentTps = new ArrayList<>();
    	List<TargetPeriodBuyOutDetailsDTO> previousTps = new ArrayList<>();
    	
    	List<TargetPeriod> periods = targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(date);

    	// TP6 might overlap with current TP
        getAvailableBuyOutTargetPeriodsForCca2(date, periods, currentTps);
        
        getAvailableBuyOutTargetPeriodsForCca3(periods, currentTps, previousTps);

        return AvailableTargetPeriodsBuyOutDTO.builder()
                .currentTargetPeriods(currentTps)
                .previousTargetPeriods(previousTps)
                .build();
	}
    
    private void getAvailableBuyOutTargetPeriodsForCca3(List<TargetPeriod> periods, List<TargetPeriodBuyOutDetailsDTO> currentTps,
			List<TargetPeriodBuyOutDetailsDTO> previousTps) {
		List<TargetPeriod> cca3Periods = periods.stream()
                .filter(tp -> SchemeVersion.CCA_3.equals(tp.getSchemeVersion()))
                .toList();

        if (cca3Periods.isEmpty()) {
            return;
        }
        
        // Get current TP
        currentTps.add(TARGET_PERIOD_MAPPER.toTargetPeriodBuyOutDetailsDTO(cca3Periods.getFirst()));
        
        // Get previous TPs
        previousTps.addAll(cca3Periods.stream()
                .skip(1)
                .map(TARGET_PERIOD_MAPPER::toTargetPeriodBuyOutDetailsDTO)
                .toList());	
	}

	private void getAvailableBuyOutTargetPeriodsForCca2(LocalDate date, List<TargetPeriod> periods,
			List<TargetPeriodBuyOutDetailsDTO> currentTps) {
		
		if (!schemeTerminationHelper.isAfterCca2TerminationDate(date)) {
			periods.stream()
			.filter(tp -> tp.getBusinessId() == TargetPeriodType.TP6)
            .findFirst()
            .map(TARGET_PERIOD_MAPPER::toTargetPeriodBuyOutDetailsDTO)
            .ifPresent(currentTps::add);
		}
	}

	private List<TargetUnitAccountBusinessInfoDTO> findEligibleAccountsForAccountPerformanceData(
			TargetPeriodType targetPeriodType) {
		return eligibleAccountsCustomRepository.findAccountsWithPerformanceDataPendingBuyOut(targetPeriodType);
	}
	
	private List<TargetUnitAccountBusinessInfoDTO> findEligibleAccountsForFacilityPerformanceData(
			TargetPeriodType targetPeriodType) {
		Set<TargetPeriodType> applicableTargetPeriods = getApplicableTargetPeriodTypes(targetPeriodType);
		
		return eligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(applicableTargetPeriods);
	}

	private Set<TargetPeriodType> getApplicableTargetPeriodTypes(TargetPeriodType targetPeriodType) {
		return determineApplicableTargetPeriods(targetPeriodType).stream()
				.map(TargetPeriodInfoDTO::getBusinessId)
				.collect(Collectors.toSet());
	}
    
    private List<TargetPeriodInfoDTO> determineApplicableTargetPeriods(TargetPeriodType targetPeriodType) {
		TargetPeriod targetPeriod = targetPeriodService.findByTargetPeriodType(targetPeriodType);
		
		return LocalDate.now().isBefore(targetPeriod.getSecondaryReportingStartDate()) 
				? List.of(TARGET_PERIOD_MAPPER.toTargetPeriodInfoDTO(targetPeriod))
						: targetPeriodService.getTargetPeriodsInfoForSchemeWithStartDateUpTo(
								targetPeriod.getSchemeVersion(),
								targetPeriod.getStartDate());
	}
	
	public Map<String, Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus>> getPerformanceDataWithBuyOutProcessedStatusPerFacility(Long accountId,  List<Long> targetPeriodIds) {
		return buyOutSurplusEligibleFacilitiesCustomRepository
	            .findFacilitiesPerformanceDataWithBuyOutProcessedStatus(accountId, targetPeriodIds)
	            .stream()
	            .collect(Collectors.groupingBy(
	                    FacilityPerformanceDataWithBuyOutProcessedStatus::getFacilityBusinessId,
	                    Collectors.toMap(
	                            FacilityPerformanceDataWithBuyOutProcessedStatus::getTargetPeriodType,
	                            Function.identity(),
	                            (existing, replacement) -> replacement,
	                            () -> new TreeMap<>(Comparator.comparingInt(TargetPeriodType::getNumber)))
	            ));
	}
	
    public List<BuyOutSurplusTransactionDetailSummary> getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(Long accountId, List<Long> targetPeriodIds) {
        return buyOutSurplusTransactionRepository.findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds);
    }
}
