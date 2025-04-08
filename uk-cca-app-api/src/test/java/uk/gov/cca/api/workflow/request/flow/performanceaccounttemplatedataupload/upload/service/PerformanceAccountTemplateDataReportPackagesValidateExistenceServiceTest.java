package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataReportPackagesValidateExistenceServiceTest {

	@InjectMocks
    private PerformanceAccountTemplateDataReportPackagesValidateExistenceService cut;
	
	@Mock
	private FileAttachmentService fileAttachmentService; 
	
	@Test
	void validateReportPackagesExistence() {
		Set<UUID> reportPackages = Set.of(
				UUID.randomUUID(),
				UUID.randomUUID()
				);
		
		when(fileAttachmentService
				.fileAttachmentsExist(reportPackages.stream().map(UUID::toString).collect(Collectors.toSet())))
				.thenReturn(true);
		
        assertDoesNotThrow(() -> cut.validateReportPackagesExistence(reportPackages));
		
		verify(fileAttachmentService, times(1))
				.fileAttachmentsExist(reportPackages.stream().map(UUID::toString).collect(Collectors.toSet()));
	}
	
	@Test
	void validateReportPackagesExistence_missing() {
		Set<UUID> reportPackages = Set.of(
				UUID.randomUUID(),
				UUID.randomUUID()
				);
		
		when(fileAttachmentService
				.fileAttachmentsExist(reportPackages.stream().map(UUID::toString).collect(Collectors.toSet())))
				.thenReturn(false);
		
		assertThrows(ReportPackageMissingException.class, () -> cut.validateReportPackagesExistence(reportPackages));

		verify(fileAttachmentService, times(1))
				.fileAttachmentsExist(reportPackages.stream().map(UUID::toString).collect(Collectors.toSet()));
	}
	
}
