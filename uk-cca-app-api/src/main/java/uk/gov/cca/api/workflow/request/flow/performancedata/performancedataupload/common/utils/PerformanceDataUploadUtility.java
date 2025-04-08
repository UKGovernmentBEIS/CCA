package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.PerformanceDataTargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class PerformanceDataUploadUtility {

    public static final String XLSX_FILE_NAME_REGEX = "^[A-Z0-9_-]+(-T\\d{5})+(_TPR_%s)+(_V\\d{0,9})+(.xlsx$)";
    public static final BigDecimal DIGIT_VALIDATION_TOLERANCE = BigDecimal.valueOf(0.000000001);
    public static final Pattern OTHER_FUEL_NAME_DEFAULT_PATTERN = Pattern.compile("^Other fuel - \\d+ - used \\([a-zA-Z]+\\)$");

    private static final String EXCEL_PATTERN_INDEX = "%c%d";
    private static final String STATUS_SUCCESS = "Success";
    private static final String STATUS_ERROR = "Error";

    public String getExcelCell(int row, int column) {
        // Alphabet for ASCII starts at 65(A)
        return String.format(EXCEL_PATTERN_INDEX, (char) (column + 65), row + 1);
    }

    public String extractBusinessAccountIdFromReportFilename(String filename) {
        return filename.substring(0, filename.lastIndexOf("_TPR"));
    }

    public Integer extractReportVersionFromReportFilename(String filename, PerformanceDataTargetPeriodType targetPeriodType) {
        final String regex = String.format(XLSX_FILE_NAME_REGEX, targetPeriodType.name());
        Matcher matcher = Pattern.compile(regex).matcher(filename);

        if(matcher.matches()) {
            String versionGroup = matcher.group(3).replace("_V", "");
            return Integer.parseInt(versionGroup);
        }

        return null;
    }

    public FileDTO createCsvFile(String requestId, List<TargetUnitAccountUploadReport> accountReports, Map<String, String> errors) throws IOException {
        // Create file name
        final String fileName = String.format("%s_Summary.csv", requestId);

        // Create CSV
        try(StringWriter sw = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                    .setHeader("TU ID", "Upload file name", "Status", "Error code and/or description")
                    .build())
        ) {

            // Successful accounts
            List<TargetUnitAccountUploadReport> succeededReports = accountReports.stream()
                    .filter(TargetUnitAccountUploadReport::isSucceeded).toList();
            for (TargetUnitAccountUploadReport accountReport : succeededReports) {
                csvPrinter.printRecord(accountReport.getAccountBusinessId(), accountReport.getFile().getName(), STATUS_SUCCESS);
            }

            // Failed accounts
            List<TargetUnitAccountUploadReport> failedReports = accountReports.stream()
                    .filter(acc -> !acc.isSucceeded()).toList();
            for (TargetUnitAccountUploadReport accountReport : failedReports) {
                for (String error : accountReport.getErrors())
                    csvPrinter.printRecord(accountReport.getAccountBusinessId(), accountReport.getFile().getName(), STATUS_ERROR, error);
            }

            // Write file errors
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                csvPrinter.printRecord(null, entry.getKey(), STATUS_ERROR, entry.getValue());
            }

            final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);

            return FileDTO.builder()
                    .fileContent(generatedFile).fileName(fileName).fileSize(generatedFile.length)
                    .fileType(MimeTypeUtils.detect(generatedFile, fileName)).build();
        }
    }
}
