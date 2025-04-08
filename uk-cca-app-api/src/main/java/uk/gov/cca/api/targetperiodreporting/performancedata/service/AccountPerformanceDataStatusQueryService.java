package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.PerformanceDataReportRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.transform.AccountPerformanceDataStatusMapper;
import uk.gov.cca.api.targetperiodreporting.performancedata.transform.AccountPerformanceReportDetailsMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.List;
import java.util.Optional;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@Service
@RequiredArgsConstructor
public class AccountPerformanceDataStatusQueryService {

    private final AccountPerformanceDataStatusRepository accountPerformanceDataStatusRepository;
	private final PerformanceDataReportRepository performanceDataReportRepository;
    private final TargetPeriodService targetPeriodService;

    private static final AccountPerformanceDataStatusMapper ACCOUNT_PERFORMANCE_DATA_STATUS_MAPPER = Mappers.getMapper(AccountPerformanceDataStatusMapper.class);
    private static final AccountPerformanceReportDetailsMapper PERFORMANCE_DATA_DETAILS_MAPPER = Mappers.getMapper(AccountPerformanceReportDetailsMapper.class);

    public List<TargetUnitAccountBusinessInfoDTO> getAccountsForPerformanceDataReportingBySector(
            Long sectorAssociationId, Long targetPeriodId, PerformanceDataSubmissionType submissionType) {
        return submissionType.equals(PerformanceDataSubmissionType.PRIMARY)
				? accountPerformanceDataStatusRepository
					.findEligibleAccountsForPrimaryPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId)
				: accountPerformanceDataStatusRepository
					.findEligibleAccountsForSecondaryPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId);
    }

	public AccountPerformanceReportDetailsDTO getAccountPerformanceReportDetails(Long accountId, TargetPeriodType targetPeriodType) {
		return PERFORMANCE_DATA_DETAILS_MAPPER
				.toPerformanceReportDetailsDTO(getLastPerformanceData(accountId, targetPeriodType));
	}

	public PerformanceBuyOutSurplusDetailsDTO getLastPerformanceBuyOutSurplusDetails(Long accountId, TargetPeriodType targetPeriodType) {
		return PERFORMANCE_DATA_DETAILS_MAPPER
				.toPerformanceBuyOutSurplusDetailsDTO(getLastPerformanceData(accountId, targetPeriodType));
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

	public SectorAccountsPerformanceReportDTO getSectorAccountsPerformanceDataReport(Long sectorAssociationId,
	                                                                                 @Valid @NotNull SectorAccountsPerformanceReportSearchCriteria criteria) {
		return performanceDataReportRepository.getSectorAccountsPerformanceReportBySearchCriteria(sectorAssociationId, criteria);
	}
	

	public Optional<PerformanceDataContainer> getLastUploadedReport(Long accountId, TargetPeriodType targetPeriodType) {
		return accountPerformanceDataStatusRepository
				.findWithDetailsByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType)
				.map(AccountPerformanceDataStatus::getLastPerformanceData)
				.map(PerformanceDataEntity::getData);
	}

	public FileInfoDTO getAccountPerformanceReportAttachment(Long accountId, TargetPeriodType targetPeriodType) {
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
