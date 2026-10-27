package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;

@UtilityClass
public class BuyOutSurplusFacilityRunUtil {
	
	private static final String STATUS_SUCCESS = "PASS";
	private static final String STATUS_ERROR = "FAIL";
    
    private static final String[] CSV_HEADERS = {
    	    "Target Unit ID",
    	    "Facility ID",
    	    "TP TPR VERSION ID",
    	    "Status",
    	    "Error Description",
    	    "TPR Calculation Type",
    	    "TPR Result",
    	    "TP Cost",
    	    "Surplus gained (tCO2e)",
    	    "Total Buy-out amount (tCO2e)",
    	    "Surplus used (tCO2e)",
            "Previous PAID Buy-out fees (GBP)",
    	    "Total Buy-out fee (GBP)",
    	    "Overpayment Buy-out fee",
    	    "Previous Surplus used (tCO2e)",
    	    "Previous Surplus gained (tCO2e)"
    	};

    public FileDTO createCsvFileContent(final String requestId, final Map<Long, BuyOutSurplusFacilityAccountState> accountStates, String createdBy) throws IOException {
        // Create file name
        final String fileName = String.format("%s Buy-out and surplus summary report.csv", requestId);

        // Create CSV
        try(StringWriter sw = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
                    .setHeader(CSV_HEADERS)
                    .build())
        ) {
            for (BuyOutSurplusFacilityAccountState accountState : accountStates.values()) {
            	if (!accountState.isSucceeded() && !accountState.getErrors().isEmpty()) {
            	    processAccountErrors(csvPrinter, accountState);
            	} else {
            	    processPerformanceDataResults(csvPrinter, accountState);
            	}
            }

            final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);

            return FileDTO.builder()
                    .fileContent(generatedFile).fileName(fileName).fileSize(generatedFile.length)
                    .fileType(MimeTypeUtils.detect(generatedFile, fileName)).createdBy(createdBy).build();
        }
    }
    
    private void processAccountErrors(CSVPrinter csvPrinter, BuyOutSurplusFacilityAccountState accountState) 
    		throws IOException {

        for (String error : accountState.getErrors()) {
            csvPrinter.printRecord(
                accountState.getAccountBusinessId(),
                "",
                "",
                STATUS_ERROR,
                error,
                "", "", "", "", "", "", "", "", "", "", ""
            );
        }
    }

	private void processPerformanceDataResults(CSVPrinter csvPrinter, BuyOutSurplusFacilityAccountState accountState)
			throws IOException {
		
		for (BuyOutSurplusFacilityPerformanceResult result : accountState.getFacilityPerformanceResults()) {
			if (result.isSucceeded()) {
				csvPrinter.printRecord(accountState.getAccountBusinessId(), 
				        result.getFacilityBusinessId(), 
						result.getTargetPeriodType().name() + "-V" + result.getPerformanceDataReportVersion(), 
						STATUS_SUCCESS,
						"",
						result.isPrimaryRulesApplied() ? "Primary" : "Secondary",
						result.getData().getPerformanceOutcome(),
						result.getTargetPeriodBuyOutCost(),
						result.getData().getSurplusGained(), 
						result.getData().getBuyOutRequired(),
						result.getData().getSurplusUsed(),  
                        result.getData().getPreviousPaidFees(),
						BuyOutSurplusChargeType.FEE.equals(result.getData().getChargeType()) ? result.getData().getInvoicedBuyOutFee() : "",
						BuyOutSurplusChargeType.REFUND.equals(result.getData().getChargeType()) ? result.getData().getInvoicedBuyOutFee() : "",
						result.getData().getPreviousSurplusUsed(), 
						result.getData().getPreviousSurplusGained()
						);
			}
			else {
				for (String error : result.getErrors()) {
					csvPrinter.printRecord(accountState.getAccountBusinessId(), 
					        result.getFacilityBusinessId(), 
		    				result.getTargetPeriodType().name() + "-V" + result.getPerformanceDataReportVersion(), 
		    				STATUS_ERROR,
		    				error,
                            "", "", "", "", "", "", "", "", "", "", "");
				}	
			}
		}
	}
}
