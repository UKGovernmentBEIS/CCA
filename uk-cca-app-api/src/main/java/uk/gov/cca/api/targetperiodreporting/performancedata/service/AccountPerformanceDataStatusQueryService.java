package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataInfo;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.PerformanceDataReportRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.transform.AccountPerformanceDataStatusMapper;
import uk.gov.cca.api.targetperiodreporting.performancedata.transform.AccountPerformanceDataReportDetailsMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@Service
@RequiredArgsConstructor
public class AccountPerformanceDataStatusQueryService {

    private final AccountPerformanceDataStatusRepository accountPerformanceDataStatusRepository;
	private final PerformanceDataReportRepository performanceDataReportRepository;
	private final AccountPerformanceDataStatusCustomRepository accountPerformanceDataStatusCustomRepository;
    private final TargetPeriodService targetPeriodService;

    private static final AccountPerformanceDataStatusMapper ACCOUNT_PERFORMANCE_DATA_STATUS_MAPPER = Mappers.getMapper(AccountPerformanceDataStatusMapper.class);
    private static final AccountPerformanceDataReportDetailsMapper PERFORMANCE_DATA_DETAILS_MAPPER = Mappers.getMapper(AccountPerformanceDataReportDetailsMapper.class);

    public List<TargetUnitAccountBusinessInfoDTO> getAccountsForPerformanceDataReportingBySector(
            Long sectorAssociationId, Long targetPeriodId) {
        return accountPerformanceDataStatusRepository
				.findEligibleAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId);
    }

	public List<AccountPerformanceDataInfo> findAccountsWithPerformanceDataForTargetPeriod(TargetPeriodType targetPeriodType) {
		return accountPerformanceDataStatusCustomRepository.findAccountsWithPerformanceDataByTargetPeriod(targetPeriodType);
	}

	public List<AccountPerformanceDataInfo> findAccountsWithPerformanceDataForTargetPeriod(TargetPeriodType targetPeriodType, Set<Long> accountIds) {
		return accountPerformanceDataStatusCustomRepository.findAccountsWithPerformanceDataByTargetPeriodAndAccountIdIn(targetPeriodType, accountIds);
	}

	public AccountPerformanceDataReportDetailsDTO getAccountPerformanceDataReportDetails(Long accountId, TargetPeriodType targetPeriodType) {
		return PERFORMANCE_DATA_DETAILS_MAPPER
				.toPerformanceDataReportDetailsDTO(getLastPerformanceData(accountId, targetPeriodType));
	}

	public PerformanceDataBuyOutSurplusDetailsDTO getLastPerformanceDataBuyOutSurplusDetails(Long accountId, TargetPeriodType targetPeriodType) {
		return PERFORMANCE_DATA_DETAILS_MAPPER
				.toPerformanceDataBuyOutSurplusDetailsDTO(getLastPerformanceData(accountId, targetPeriodType));
	}

	public AccountPerformanceDataStatusInfoDTO getAccountPerformanceDataStatusInfo(Long accountId,
			TargetPeriodType targetPeriodType, AppUser currentUser) {

		AccountPerformanceDataStatusInfoDTO infoDTO = accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType)
				.map(ACCOUNT_PERFORMANCE_DATA_STATUS_MAPPER::toAccountPerformanceDataStatusInfoDTO)
				.orElseGet(() -> AccountPerformanceDataStatusInfoDTO.builder().reportVersion(0).locked(false)
						.targetPeriodName(targetPeriodService.findTargetPeriodNameByTargetPeriodType(targetPeriodType))
						.build());

		infoDTO.setEditable(REGULATOR.equals(currentUser.getRoleType()));

        return infoDTO;
    }

	public SectorAccountPerformanceDataReportListDTO getSectorAccountPerformanceDataReportList(Long sectorAssociationId,
	                                                                                 @Valid @NotNull SectorAccountPerformanceDataReportSearchCriteria criteria) {
		return performanceDataReportRepository.getSectorAccountPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);
	}
	

	public Optional<PerformanceDataContainer> getLastUploadedPerformanceData(Long accountId, TargetPeriodType targetPeriodType) {
		return accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType)
				.map(AccountPerformanceDataStatus::getLastPerformanceData)
				.map(PerformanceDataEntity::getData);
	}

	public FileInfoDTO getAccountPerformanceDataReportAttachment(Long accountId, TargetPeriodType targetPeriodType) {
		return getLastPerformanceData(accountId, targetPeriodType).getData().getTargetPeriodReport();
	}

	public int getNextAccountPerformanceDataReportVersion(Long accountId, TargetPeriodType targetPeriodType) {
		return accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType)
				.map(status -> status.getLastPerformanceData().getReportVersion() + 1)
				.orElse(1);
	}

    private PerformanceDataEntity getLastPerformanceData(Long accountId, TargetPeriodType targetPeriodType) {
        return this.getAccountPerformanceDataStatus(accountId, targetPeriodType)
                .getLastPerformanceData();
    }

    private AccountPerformanceDataStatus getAccountPerformanceDataStatus(Long accountId,
                                                                         TargetPeriodType targetPeriodType) {
        return accountPerformanceDataStatusRepository
                .findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
