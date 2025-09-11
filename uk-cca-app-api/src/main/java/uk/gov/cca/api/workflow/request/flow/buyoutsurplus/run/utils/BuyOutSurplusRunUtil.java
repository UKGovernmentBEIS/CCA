package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@UtilityClass
public class BuyOutSurplusRunUtil {

    private final String STATUS_SUCCESS = "PASS";
    private final String STATUS_ERROR = "FAIL";

    public FileDTO createCsvFileContent(final String requestId, final Map<Long, BuyOutSurplusAccountState> buyOutSurplusAccountStates) throws IOException {
        // Create file name
        final String fileName = String.format("%s Buy-out and surplus summary report.csv", requestId);

        // Create CSV
        try(StringWriter sw = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                    .setHeader("Target Unit ID", "Status", "Error Description")
                    .build())
        ) {
            for (BuyOutSurplusAccountState accountState : buyOutSurplusAccountStates.values()) {
                if(accountState.isSucceeded()) {
                    csvPrinter.printRecord(accountState.getBusinessId(), STATUS_SUCCESS);
                }
                else {
                    for (String error : accountState.getErrors()) {
                        csvPrinter.printRecord(accountState.getBusinessId(), STATUS_ERROR, error);
                    }
                }
            }

            final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);

            return FileDTO.builder()
                    .fileContent(generatedFile).fileName(fileName).fileSize(generatedFile.length)
                    .fileType(MimeTypeUtils.detect(generatedFile, fileName)).build();
        }
    }
}
