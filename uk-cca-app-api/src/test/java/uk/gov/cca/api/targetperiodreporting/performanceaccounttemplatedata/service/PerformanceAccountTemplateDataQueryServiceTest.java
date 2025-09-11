package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository.PerformanceAccountTemplateDataCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository.PerformanceAccountTemplateDataRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataQueryServiceTest {

	@InjectMocks
	private PerformanceAccountTemplateDataQueryService cut;

	@Mock
	private PerformanceAccountTemplateDataRepository repo;
	
	@Mock
	private PerformanceAccountTemplateDataCustomRepository customRepo;
	
	@Test
	void calculateNextReportVersion() {
		Long accountId = 1L;
		Year targetPeriodYear = Year.of(2024);
		
		when(repo.findReportVersionByAccountIdAndTargetPeriodYear(accountId, targetPeriodYear)).thenReturn(4);
		
		var result = cut.calculateNextReportVersion(accountId, targetPeriodYear);
		
		assertThat(result).isEqualTo(5);
		
		verify(repo, times(1)).findReportVersionByAccountIdAndTargetPeriodYear(accountId, targetPeriodYear);
	}
	
	@Test
	void getSectorAccountsDataReportList() {
		Long sectorAssociationId = 1L;
		SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.build();
		Year targetPeriodYear = Year.of(2024);
		
		SectorPerformanceAccountTemplateDataReportListDTO listDTO = SectorPerformanceAccountTemplateDataReportListDTO.builder()
				.items(List.of(SectorPerformanceAccountTemplateDataReportItemDTO.builder()
						.operatorName("dfd")
						.build()))
				.total(1L)
				.build();
		
		when(customRepo.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(sectorAssociationId, criteria,
				targetPeriodYear)).thenReturn(listDTO);

		var result = cut.getSectorAccountsDataReportList(sectorAssociationId, criteria, targetPeriodYear);
		
		assertThat(result).isEqualTo(listDTO);
		
		verify(customRepo, times(1)).getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(
				sectorAssociationId, criteria, targetPeriodYear);
	}
	
	@Test
	void findReportInfoByAccountIdAndTargetPeriod() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		PerformanceAccountTemplateDataEntity patEntity = PerformanceAccountTemplateDataEntity.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).name("test").build())
				.build();
		
		when(repo.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType))
				.thenReturn(Optional.of(patEntity));
		
		var result = cut.findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType);
		
		assertThat(result).isEqualTo(Optional.of(AccountPerformanceAccountTemplateDataReportInfoDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.build()));
		
		verify(repo, times(1)).findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType);
	}
	
	@Test
	void getReportDetailsByAccountIdAndTargetPeriod() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		PerformanceAccountTemplateDataContainer patContainer = PerformanceAccountTemplateDataContainer.builder()
				.file(FileInfoDTO.builder().name("filename").uuid("uuid").build())
				.build();
		
		PerformanceAccountTemplateDataEntity patEntity = PerformanceAccountTemplateDataEntity.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).name("test").build())
				.data(patContainer)
				.build();
		
		when(repo.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType))
				.thenReturn(Optional.of(patEntity));
		
		var result = cut.getReportDetailsByAccountIdAndTargetPeriod(accountId, targetPeriodType);
		
		assertThat(result).isEqualTo(AccountPerformanceAccountTemplateDataReportDetailsDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.data(patContainer)
				.build());
		
		verify(repo, times(1)).findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType);
	}
	
	@Test
	void getReportDetailsByAccountIdAndTargetPeriod_not_found_exception() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		when(repo.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType))
				.thenReturn(Optional.empty());
		
		var be = assertThrows(BusinessException.class,
				() -> cut.getReportDetailsByAccountIdAndTargetPeriod(accountId, targetPeriodType));
		
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
		
		verify(repo, times(1)).findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType);
	}
	
	@Test
	void getAttachmentReportByAccountIdAndTargetPeriod() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		PerformanceAccountTemplateDataContainer patContainer = PerformanceAccountTemplateDataContainer.builder()
				.file(FileInfoDTO.builder().name("filename").uuid("uuid").build())
				.build();
		
		PerformanceAccountTemplateDataEntity patEntity = PerformanceAccountTemplateDataEntity.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).name("test").build())
				.data(patContainer)
				.build();
		
		when(repo.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType))
				.thenReturn(Optional.of(patEntity));
		
		var result = cut.getAttachmentReportByAccountIdAndTargetPeriod(accountId, targetPeriodType);
		
		assertThat(result).isEqualTo(FileInfoDTO.builder()
				.name("filename").uuid("uuid")
				.build());
		
		verify(repo, times(1)).findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType);
	}
	
}
