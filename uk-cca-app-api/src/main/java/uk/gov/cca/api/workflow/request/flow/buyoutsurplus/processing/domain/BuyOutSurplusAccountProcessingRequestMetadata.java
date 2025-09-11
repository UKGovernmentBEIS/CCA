package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusAccountProcessingRequestMetadata extends RequestMetadata {
    private String parentRequestId;
    private String accountBusinessId;
    private TargetPeriodType targetPeriodType;
    private Long performanceDataId;
    private Integer performanceDataReportVersion;
    private TargetPeriodResultType tpOutcome;
    private String transactionCode;
}
