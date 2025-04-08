package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.validation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.cca.api.common.validation.BusinessViolation;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PerformanceDataDownloadViolation extends BusinessViolation {

    private String message;

    public PerformanceDataDownloadViolation(PerformanceDataDownloadViolationMessage violationMessage) {
        super("", List.of());
        this.message = violationMessage.getMessage();
    }

    @Getter
    public enum PerformanceDataDownloadViolationMessage {
        PROCESS_NOT_COMPLETED("Download process not completed"),
        GENERATE_EXCEL_FAILED("Generate excel failed"),
        GENERATE_ZIP_FAILED("Failed to generate main zip file"),
        GENERATE_CSV_FAILED("Failed to generate error csv file"),
        NO_ELIGIBLE_ACCOUNTS_FOR_TPR_REPORTING("No eligible accounts found for the target period"),
        ;

        private final String message;

        PerformanceDataDownloadViolationMessage(String message) {
            this.message = message;
        }
    }
}
