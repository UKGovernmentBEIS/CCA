package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import uk.gov.cca.api.common.utils.CsvUtils;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@UtilityClass
public class FacilityPerformanceAccountTemplateDataUploadUtility {

    private static final String CSV_RESULT = "Upload_Summary.csv";
    private static final String STATUS_SUCCESS = "Success";
    private static final String STATUS_ERROR = "Error";

    public FileDTO createCsvFile(List<FacilityPerformanceAccountTemplateUploadReport> facilityReports,
                                 List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors,
                                 String createdBy) throws IOException {
        // Create CSV
        try (StringWriter sw = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                     .setHeader("Facility ID", "Upload file name", "Status", "Row Number", "Error code and/or description")
                     .build())
        ) {

            // Successful facilities
            List<FacilityPerformanceAccountTemplateUploadReport> succeededReports = facilityReports.stream()
                    .filter(FacilityPerformanceAccountTemplateUploadReport::isSucceeded).toList();
            for (FacilityPerformanceAccountTemplateUploadReport facilityReport : succeededReports) {
                csvPrinter.printRecord(facilityReport.getFacilityBusinessId(), "", STATUS_SUCCESS);
            }

            // Failed facilities
            List<FacilityPerformanceAccountTemplateUploadReport> failedReports = facilityReports.stream()
                    .filter(acc -> !acc.isSucceeded()).toList();
            for (FacilityPerformanceAccountTemplateUploadReport facilityReport : failedReports) {
                String error = String.join(CsvUtils.CSV_ERROR_DELIMITER, facilityReport.getErrors());
                csvPrinter.printRecord(facilityReport.getFacilityBusinessId(), "", STATUS_ERROR, "", error);
            }

            // Write file errors
            for (FacilityPerformanceAccountTemplateDataCsvErrorEntry entry : csvRowErrors) {
                csvPrinter.printRecord(entry.getFacilityBusinessId(), entry.getFilename(), STATUS_ERROR, entry.getRowNumber(), entry.getMessage());
            }

            final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);

            return FileDTO.builder()
                    .fileContent(generatedFile).fileName(CSV_RESULT).fileSize(generatedFile.length)
                    .fileType(MimeTypeUtils.detect(generatedFile, CSV_RESULT)).createdBy(createdBy).build();
        }
    }
}