package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service;

import java.time.Year;
import java.util.Optional;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository.PerformanceAccountTemplateDataCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository.PerformanceAccountTemplateDataRepository;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.transform.PerformanceAccountTemplateMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataQueryService {

	private final PerformanceAccountTemplateDataRepository repo;
	private final PerformanceAccountTemplateDataCustomRepository customRepo;
    private static final PerformanceAccountTemplateMapper MAPPER = Mappers.getMapper(PerformanceAccountTemplateMapper.class);

	public SectorPerformanceAccountTemplateDataReportListDTO getSectorPerformanceAccountTemplateDataReportListDTO(
			Long sectorAssociationId, SectorPerformanceAccountTemplateDataReportSearchCriteria criteria) {
		final int reportYear = 2024;
		final Year targetPeriodYear = Year.of(reportYear); //TODO make it configurable
		if(!criteria.getTargetPeriodType().equals(TargetPeriodType.TP6)) {
			throw new RuntimeException("cannot display pat reports");
		}

		return customRepo.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(
				sectorAssociationId, criteria, targetPeriodYear);
	}

	public int calculateNextReportVersion(Long accountId, Year targetPeriodYear) {
		int reportVersion = repo.findReportVersionByAccountIdAndTargetPeriodYear(accountId, targetPeriodYear);
		return ++reportVersion;
	}

	public Optional<AccountPerformanceAccountTemplateDataReportInfoDTO> findReportInfoByAccountIdAndTargetPeriod(Long accountId,
			TargetPeriodType targetPeriodType) {
		return repo.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType)
				.map(MAPPER::toReportInfoDTO);
	}
	
	public AccountPerformanceAccountTemplateDataReportDetailsDTO getReportDetailsByAccountIdAndTargetPeriod(Long accountId,
			TargetPeriodType targetPeriodType) {
		return repo.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType)
				.map(MAPPER::toReportDetailsDTO)
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
	}
	
	public FileInfoDTO getAttachmentReportByAccountIdAndTargetPeriod(Long accountId,
			TargetPeriodType targetPeriodType) {
		return repo.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType)
				.map(pat -> pat.getData().getFile())
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
	}
}
