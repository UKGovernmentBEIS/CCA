package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class SubsistenceFeesRunGenerateReportService {
	
	private static final DateTimeFormatter CSV_DATE_ISSUE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.ENGLISH);
	
	private final RequestService requestService;
	private final FileDocumentService fileDocumentService;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generateReport(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final SubsistenceFeesRunRequestPayload payload = (SubsistenceFeesRunRequestPayload) request.getPayload();
		final SubsistenceFeesRunRequestMetadata metadata = (SubsistenceFeesRunRequestMetadata) request.getMetadata();
		List<MoaReport> allReports = metadata.getAllReports();
		
		// Generate CSV report only if at least one report exists
		if (!allReports.isEmpty()) {
			try (StringWriter sw = new StringWriter();
					CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
							.setHeader("MoA Type", 
									"Sector ID", 
									"Sector name", 
									"Target Unit ID",
									"Operator Name",
									"Status",
									"Date Issued",
									"Error Description")
							.build());) {

				for (MoaReport moaReport : allReports) {
					csvPrinter.printRecord(
							moaReport.getMoaType().getDescription(),
							moaReport.getSectorAcronym(),
							moaReport.getSectorName(),
							moaReport.getBusinessId(),
							moaReport.getOperatorName(),
							Boolean.TRUE.equals(moaReport.getSucceeded()) ? "PASS" : "FAIL",
							moaReport.getIssueDate() != null
									? CSV_DATE_ISSUE_FORMATTER.format(moaReport.getIssueDate())
									: "N/A",
							!moaReport.getErrors().isEmpty() 
									? moaReport.getErrors()
									: null
									);
				}
				
				final byte[] generatedFile = sw.toString().getBytes("UTF-8");
				final FileInfoDTO reportFile = fileDocumentService.createFileDocument(generatedFile, request.getId() + " subsistence fees summary report.csv");
				
				//update payload
				payload.setReport(reportFile);
			} catch (Exception e) {
				log.error(String.format("Cannot generate csv report for request %s", requestId), e);
			}
		}	
	}
}
