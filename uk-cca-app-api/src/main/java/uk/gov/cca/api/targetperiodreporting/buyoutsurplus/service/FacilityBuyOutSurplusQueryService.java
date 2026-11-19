package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailsSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusEligibleAccountsCustomRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusExclusionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionDetailRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusTransactionRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusTransactionMapper;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.transform.TargetPeriodMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.storage.FileDocumentStorageService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FacilityBuyOutSurplusQueryService extends AbstractBuyOutSurplusQueryService implements BuyOutSurplusQueryService {

    private final TargetUnitAccountQueryService targetUnitAccountQueryService;

    private static final TargetPeriodMapper TARGET_PERIOD_MAPPER = Mappers.getMapper(TargetPeriodMapper.class);
    private static final BuyOutSurplusTransactionMapper BUY_OUT_SURPLUS_MAPPER = Mappers.getMapper(BuyOutSurplusTransactionMapper.class);

    public FacilityBuyOutSurplusQueryService(
            BuyOutSurplusExclusionRepository buyOutSurplusExclusionRepository,
            BuyOutSurplusTransactionDetailRepository buyOutSurplusTransactionDetailRepository,
            TargetPeriodService targetPeriodService,
            BuyOutSurplusEligibleAccountsCustomRepository buyOutSurplusEligibleAccountsCustomRepository,
            BuyOutSurplusTransactionRepository buyOutSurplusTransactionRepository,
            FileDocumentStorageService fileDocumentStorageService,
            TargetUnitAccountQueryService targetUnitAccountQueryService) {

        super(buyOutSurplusExclusionRepository, buyOutSurplusTransactionDetailRepository, targetPeriodService,
                buyOutSurplusEligibleAccountsCustomRepository, buyOutSurplusTransactionRepository, fileDocumentStorageService);
        this.targetUnitAccountQueryService = targetUnitAccountQueryService;
    }

    @Override
    protected List<TargetUnitAccountBusinessInfoDTO> findEligibleAccountsForPerformanceData(TargetPeriodType targetPeriodType) {
        Set<TargetPeriodType> applicableTargetPeriods = determineApplicableTargetPeriods(targetPeriodType).stream()
                .map(TargetPeriodInfoDTO::getBusinessId)
                .collect(Collectors.toSet());

        return buyOutSurplusEligibleAccountsCustomRepository.findAccountsWithFacilityPerformanceDataPendingBuyOut(applicableTargetPeriods);
    }

    @Transactional(readOnly = true)
    public FacilityBuyOutSurplusTransactionDTO getBuyOutSurplusTransaction(Long id) {
        BuyOutSurplusTransaction transaction = buyOutSurplusTransactionRepository
                .findBuyOutSurplusTransactionByIdAndPerformanceDataResourceType(id, this.getResourceType())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        TargetUnitAccountBusinessInfoDTO account = targetUnitAccountQueryService
                .getTargetUnitAccountBusinessInfoDTO(transaction.getAccountId());
        FileInfoDTO fileInfoDTO = fileDocumentStorageService.getFileInfoDTO(transaction.getFileDocumentUuid());

        return BUY_OUT_SURPLUS_MAPPER
                .toFacilityBuyOutSurplusTransactionDTO(transaction, account, fileInfoDTO);
    }

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

    public List<BuyOutSurplusTransactionDetailSummary> getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(Long accountId, List<Long> targetPeriodIds) {
        return buyOutSurplusTransactionRepository.findTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds);
    }

    @Override
    public PerformanceDataResourceType getResourceType() {
        return PerformanceDataResourceType.FACILITY;
    }

    private List<TargetPeriodInfoDTO> determineApplicableTargetPeriods(TargetPeriodType targetPeriodType) {
        TargetPeriod targetPeriod = targetPeriodService.findByTargetPeriodType(targetPeriodType);

        return LocalDate.now().isBefore(targetPeriod.getSecondaryReportingStartDate())
                ? List.of(TARGET_PERIOD_MAPPER.toTargetPeriodInfoDTO(targetPeriod))
                : targetPeriodService.getTargetPeriodsInfoForSchemeWithStartDateUpTo(targetPeriod.getSchemeVersion(), targetPeriod.getStartDate());
    }
}
