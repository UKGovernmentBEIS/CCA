package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.gov.cca.api.common.utils.CsvUtils;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityViolation;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityCsvErrorEntry;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PerformanceDataFacilityDataUploadUtility {

    private static final String CSV_RESULT = "Upload_Summary.csv";
    private static final String STATUS_SUCCESS = "Success";
    private static final String STATUS_ERROR = "Error";
    private static final String FACILITY_ERROR_MESSAGE = "%s: [%s]";

    public FileDTO createCsvFile(List<FacilityUploadReport> facilityReports, List<PerformanceDataFacilityCsvErrorEntry> csvRowErrors) throws IOException {
        // Create CSV
        try(StringWriter sw = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                    .setHeader("Facility ID", "Upload file name", "Status", "Error code and/or description")
                    .build())
        ) {

            // Successful facilities
            List<FacilityUploadReport> succeededReports = facilityReports.stream()
                    .filter(FacilityUploadReport::isSucceeded).toList();
            for (FacilityUploadReport facilityReport : succeededReports) {
                csvPrinter.printRecord(facilityReport.getFacilityBusinessId(), facilityReport.getCsvFileName(), STATUS_SUCCESS);
            }

            // Failed facilities
            List<FacilityUploadReport> failedReports = facilityReports.stream()
                    .filter(acc -> !acc.isSucceeded()).toList();
            for (FacilityUploadReport facilityReport : failedReports) {
                String error = String.join(CsvUtils.CSV_ERROR_DELIMITER, facilityReport.getErrors());
                csvPrinter.printRecord(facilityReport.getFacilityBusinessId(), facilityReport.getCsvFileName(), STATUS_ERROR, error);
            }

            // Write file errors
            for (PerformanceDataFacilityCsvErrorEntry entry : csvRowErrors) {
                csvPrinter.printRecord(entry.getFacilityBusinessId(), entry.getFilename(), STATUS_ERROR, entry.getMessage());
            }

            final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);

            return FileDTO.builder()
                    .fileContent(generatedFile).fileName(CSV_RESULT).fileSize(generatedFile.length)
                    .fileType(MimeTypeUtils.detect(generatedFile, CSV_RESULT)).build();
        }
    }

    public List<String> createFacilityUploadReportErrors(List<BusinessValidationResult> results) {
        List<String> errors = new ArrayList<>();

        // Convert to PerformanceDataFacilityViolation
        List<PerformanceDataFacilityViolation> violations = results.stream()
                .map(BusinessValidationResult::getViolations)
                .flatMap(List::stream)
                .map(violation -> new PerformanceDataFacilityViolation(violation.getSectionName(), violation.getData()))
                .toList();

        // Convert to string for FacilityUploadReport errors
        violations.forEach(violation -> {
            String result = java.util.Arrays.stream(violation.getData())
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.joining(", "));
            errors.add(String.format(FACILITY_ERROR_MESSAGE,
                    PerformanceDataFacilityViolation.PerformanceDataFacilityViolationMessage.INVALID_PERFORMANCE_DATA.getMessage(), result));
        });

        return errors;
    }
}
