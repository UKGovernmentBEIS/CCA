package uk.gov.cca.api.workflow.request.flow.performancedata.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import uk.gov.netz.api.common.exception.BusinessException;

import java.util.Arrays;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Getter
@AllArgsConstructor
public enum TargetPeriodDocumentTemplate {

    REPORTING_SPREADSHEETS_DOWNLOAD_TP6(PerformanceDataTargetPeriodType.TP6);

    private final PerformanceDataTargetPeriodType targetPeriodType;

    public static TargetPeriodDocumentTemplate getTargetPeriodDocumentTemplate(PerformanceDataTargetPeriodType targetPeriodType) {
        return Arrays.stream(TargetPeriodDocumentTemplate.values())
                .filter(t -> t.targetPeriodType.equals(targetPeriodType))
                .findFirst().orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
    }
}
