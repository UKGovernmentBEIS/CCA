package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPerformanceReportDetailsDTO {

    // Details
    private FileInfoDTO targetPeriodReport;

    private LocalDateTime submissionDate;

    private PerformanceDataSubmissionType submissionType;

    private int reportVersion;

    // TP Result
    private MeasurementType energyCarbonUnit; // unit

    private AgreementCompositionType targetType;

    private String throughputUnit;

    // Target Period Energy
    private BigDecimal tpPerformance;

    // Percent improvement target
    private BigDecimal percentTarget;

    // Target Period Improvement Relative to Base Year %
    private BigDecimal tpPerformancePercent;

    // Target Period Result
    private TargetPeriodResultType tpOutcome;

    private CarbonSurplusBuyOutDTO carbonSurplusBuyOutDTO;

    private SecondaryMoASurplusBuyOutDTO secondaryMoASurplusBuyOutDTO;
}
