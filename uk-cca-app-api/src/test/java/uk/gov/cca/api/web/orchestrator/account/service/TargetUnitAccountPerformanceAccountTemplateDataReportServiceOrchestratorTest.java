package uk.gov.cca.api.web.orchestrator.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataAttachmentService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.token.FileToken;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountPerformanceAccountTemplateDataReportServiceOrchestratorTest {

	@InjectMocks
	private TargetUnitAccountPerformanceAccountTemplateDataReportServiceOrchestrator cut;
	
	@Mock
	private PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;
	
	@Mock
	private PerformanceAccountTemplateDataAttachmentService performanceAccountTemplateDataAttachmentService;
	
	@Test
	void getReportInfoDTO() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		AccountPerformanceAccountTemplateDataReportInfoDTO reportDto = AccountPerformanceAccountTemplateDataReportInfoDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.build();
		
		when(performanceAccountTemplateDataQueryService.findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType))
				.thenReturn(Optional.of(reportDto));
		
		var result = cut.getReportInfoDTO(accountId, targetPeriodType);
		
		assertThat(result).isEqualTo(reportDto);
		
		verify(performanceAccountTemplateDataQueryService, times(1)).findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType);
	}
	
	@Test
	void getReportInfoDTO_empty() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		when(performanceAccountTemplateDataQueryService.findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType))
				.thenReturn(Optional.empty());
		
		var result = cut.getReportInfoDTO(accountId, targetPeriodType);
		
		assertThat(result).isNull();
		
		verify(performanceAccountTemplateDataQueryService, times(1)).findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType);
	}
	
	@Test
	void getReportDetailsDTO() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		AccountPerformanceAccountTemplateDataReportDetailsDTO reportDto = AccountPerformanceAccountTemplateDataReportDetailsDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.data(PerformanceAccountTemplateDataContainer.builder().file(FileInfoDTO.builder().name("dfd").build()).build())
				.build();
		
		when(performanceAccountTemplateDataQueryService.getReportDetailsByAccountIdAndTargetPeriod(accountId, targetPeriodType))
				.thenReturn(reportDto);
		
		var result = cut.getReportDetailsDTO(accountId, targetPeriodType);
		
		assertThat(result).isEqualTo(reportDto);
		
		verify(performanceAccountTemplateDataQueryService, times(1)).getReportDetailsByAccountIdAndTargetPeriod(accountId, targetPeriodType);
	}
	
	@Test
	void generateGetReportAttachmentToken() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		UUID fileAttachmentUuid = UUID.randomUUID();
		
		when(performanceAccountTemplateDataAttachmentService.generateGetAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid))
			.thenReturn(FileToken.builder().token("token").build());

		var result = cut.generateGetReportAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid);
		
		assertThat(result).isEqualTo(FileToken.builder().token("token").build());

		verify(performanceAccountTemplateDataAttachmentService, times(1)).generateGetAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid);
	}
	
}
