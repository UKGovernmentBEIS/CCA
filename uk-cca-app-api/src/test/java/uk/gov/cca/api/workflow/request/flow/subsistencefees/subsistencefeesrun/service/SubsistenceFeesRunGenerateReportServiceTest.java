package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunGenerateReportServiceTest {

	@InjectMocks
	private SubsistenceFeesRunGenerateReportService service;

	@Mock
	private RequestService requestService;
	
	@Mock
	private FileDocumentService fileDocumentService;
	
	@Test
	void generateReport() {
		String requestId = "req";
		LocalDate issueDate1 = LocalDate.of(2025, 5, 12);
		SubsistenceFeesRunRequestPayload payload = SubsistenceFeesRunRequestPayload.builder()
				.build();
		Map<Long, MoaReport> accountsReports = Map.of(
				1L, MoaReport.builder()
						.businessId("businessId1")
						.operatorName("oper1")
						.moaType(MoaType.TARGET_UNIT_MOA)
						.succeeded(true)
						.issueDate(issueDate1)
						.build(),
				2L, MoaReport.builder()
						.businessId("businessId2")
						.operatorName("oper2")
						.moaType(MoaType.TARGET_UNIT_MOA)
						.succeeded(false)
						.build());
		
		Map<Long, MoaReport> sectorsReports = Map.of(
				1L, MoaReport.builder()
						.sectorAcronym("acronym1")
						.sectorName("sector1")
						.moaType(MoaType.SECTOR_MOA)
						.succeeded(true)
						.issueDate(issueDate1)
						.build(),
				2L, MoaReport.builder()
						.sectorAcronym("acronym2")
						.sectorName("sector2")
						.moaType(MoaType.SECTOR_MOA)
						.succeeded(false)
						.build());
		
		SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
				.sectorsReports(sectorsReports)
				.accountsReports(accountsReports)
				.build();
		final Request request = Request.builder()
                .id(requestId)
                .payload(payload)
                .metadata(metadata)
                .build();
    	
		FileInfoDTO reportFile = FileInfoDTO.builder()
				.name("rep")
				.uuid(UUID.randomUUID().toString())
				.build();
		
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(fileDocumentService.createFileDocument(Mockito.any(), Mockito.eq("req subsistence fees summary report.csv"))).thenReturn(reportFile);
    	
    	service.generateReport(requestId);
    	
    	assertThat(payload.getReport()).isEqualTo(reportFile);
    	
    	verify(requestService, times(1)).findRequestById(requestId);
    	ArgumentCaptor<byte[]> fileContentCaptor = ArgumentCaptor.forClass(byte[].class);
    	verify(fileDocumentService, times(1)).createFileDocument(fileContentCaptor.capture(), Mockito.eq("req subsistence fees summary report.csv"));
    	byte[] fileContentCaptured = fileContentCaptor.getValue();
    	String fileContentAsStringCaptured = new String(fileContentCaptured);
    	
    	Scanner scanner = new Scanner(fileContentAsStringCaptured);
    	List<String> lines = new ArrayList<>();
    	while (scanner.hasNextLine()) {
    	  lines.add(scanner.nextLine());
    	}
    	scanner.close();
    	
    	assertThat(lines).containsExactlyInAnyOrder("MoA Type,Sector ID,Sector name,Target Unit ID,Operator Name,Status,Date Issued,Error Description",
    			"Sector MoA,acronym1,sector1,,,PASS,12-May-2025,",
    			"Sector MoA,acronym2,sector2,,,FAIL,N/A,",
    			"Target Unit MoA,,,businessId1,oper1,PASS,12-May-2025,",
    			"Target Unit MoA,,,businessId2,oper2,FAIL,N/A,"
    			);
	}
}
