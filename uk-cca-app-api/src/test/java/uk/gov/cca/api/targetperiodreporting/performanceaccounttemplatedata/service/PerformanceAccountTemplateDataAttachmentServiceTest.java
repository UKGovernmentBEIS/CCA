package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.token.FileToken;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataAttachmentServiceTest {

	@InjectMocks
	private PerformanceAccountTemplateDataAttachmentService cut;

	@Mock
	private PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;

	@Mock
	private FileAttachmentTokenService fileAttachmentTokenService;

	@Test
	void generateGetAttachmentToken() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		UUID fileAttachmentUuid = UUID.randomUUID();

		FileInfoDTO fileReport = FileInfoDTO.builder().name("token").uuid(fileAttachmentUuid.toString()).build();

		FileToken fileToken = FileToken.builder().token("token").build();

		when(performanceAccountTemplateDataQueryService.getAttachmentReportByAccountIdAndTargetPeriod(accountId,
				targetPeriodType)).thenReturn(fileReport);
		when(fileAttachmentTokenService.generateGetFileAttachmentToken(fileAttachmentUuid.toString()))
				.thenReturn(fileToken);

		var result = cut.generateGetAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid);

		assertThat(result).isEqualTo(fileToken);

		verify(performanceAccountTemplateDataQueryService, times(1))
				.getAttachmentReportByAccountIdAndTargetPeriod(accountId, targetPeriodType);
		verify(fileAttachmentTokenService, times(1)).generateGetFileAttachmentToken(fileAttachmentUuid.toString());
	}
	
	@Test
	void generateGetAttachmentToken_file_uuid_irrelevant() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		UUID fileAttachmentUuid = UUID.randomUUID();

		FileInfoDTO fileReport = FileInfoDTO.builder().name("token").uuid(fileAttachmentUuid.toString()).build();

		when(performanceAccountTemplateDataQueryService.getAttachmentReportByAccountIdAndTargetPeriod(accountId,
				targetPeriodType)).thenReturn(fileReport);

		BusinessException be = assertThrowsExactly(BusinessException.class,
				() -> cut.generateGetAttachmentToken(accountId, targetPeriodType, UUID.randomUUID()));

		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

		verify(performanceAccountTemplateDataQueryService, times(1))
				.getAttachmentReportByAccountIdAndTargetPeriod(accountId, targetPeriodType);
		verifyNoInteractions(fileAttachmentTokenService);
	}

}
