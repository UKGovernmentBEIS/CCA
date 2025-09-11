package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusDetails {

    @NotNull
    private TargetPeriodType targetPeriodType;

    @NotNull
    private Integer performanceDataReportVersion;

    @NotNull
    private PerformanceDataSubmissionType submissionType;

    @NotNull
    private TargetPeriodResultType tpOutcome;

    private BuyOutSurplusPaymentStatus paymentStatus;

    private String transactionCode;

    private FileInfoDTO officialNotice;

    @FutureOrPresent
    private LocalDate dueDate;

    @NotBlank
    private String runId;
}
