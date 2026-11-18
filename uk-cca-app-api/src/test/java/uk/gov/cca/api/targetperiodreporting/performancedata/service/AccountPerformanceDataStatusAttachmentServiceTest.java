package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

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

class AccountPerformanceDataStatusAttachmentServiceTest {

	@InjectMocks
	private AccountPerformanceDataStatusAttachmentService accountPerformanceDataStatusAttachmentService;

	@Mock
	private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

	@Mock
	private FileAttachmentTokenService fileAttachmentTokenService;

	@Test
	void generateGetFileAttachmentToken() {

		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final UUID fileAttachmentUuid = UUID.randomUUID();

		final FileInfoDTO performanceDataReport = FileInfoDTO.builder().uuid(fileAttachmentUuid.toString()).build();

		final FileToken fileToken = FileToken.builder().token("token").build();

		when(accountPerformanceDataStatusQueryService.getAccountPerformanceDataReportAttachment(accountId,
				targetPeriodType)).thenReturn(performanceDataReport);
		when(fileAttachmentTokenService.generateGetFileAttachmentToken(fileAttachmentUuid.toString()))
				.thenReturn(fileToken);

		final FileToken result = accountPerformanceDataStatusAttachmentService.generateGetAccountPerformanceDataReportAttachmentToken(accountId,
				targetPeriodType, fileAttachmentUuid);

		assertEquals(result, fileToken);
		verify(accountPerformanceDataStatusQueryService, times(1)).getAccountPerformanceDataReportAttachment(accountId,
				targetPeriodType);
		verify(fileAttachmentTokenService, times(1)).generateGetFileAttachmentToken(fileAttachmentUuid.toString());
	}

	@Test
	void generateGetFileAttachmentToken_uuid_not_match() {

		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final UUID fileAttachmentUuid = UUID.randomUUID();
		final UUID actualFileAttachmentUuid = UUID.randomUUID();

		final FileInfoDTO performanceDataReport = FileInfoDTO.builder().uuid(actualFileAttachmentUuid.toString()).build();

		when(accountPerformanceDataStatusQueryService.getAccountPerformanceDataReportAttachment(accountId,
				targetPeriodType)).thenReturn(performanceDataReport);

		BusinessException businessException = assertThrows(BusinessException.class,
				() -> accountPerformanceDataStatusAttachmentService.generateGetAccountPerformanceDataReportAttachmentToken(accountId,
						targetPeriodType, fileAttachmentUuid));

		assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
		verify(accountPerformanceDataStatusQueryService, times(1)).getAccountPerformanceDataReportAttachment(accountId,
				targetPeriodType);
		verifyNoInteractions(fileAttachmentTokenService);
	}

	@Test
	void generateGetFileAttachmentToken_uuid_not_found() {

		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final UUID fileAttachmentUuid = UUID.randomUUID();

		when(accountPerformanceDataStatusQueryService.getAccountPerformanceDataReportAttachment(accountId,
				targetPeriodType)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

		BusinessException businessException = assertThrows(BusinessException.class,
				() -> accountPerformanceDataStatusAttachmentService.generateGetAccountPerformanceDataReportAttachmentToken(accountId,
						targetPeriodType, fileAttachmentUuid));

		assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
		verify(accountPerformanceDataStatusQueryService, times(1)).getAccountPerformanceDataReportAttachment(accountId,
				targetPeriodType);
		verifyNoInteractions(fileAttachmentTokenService);
	}
}
