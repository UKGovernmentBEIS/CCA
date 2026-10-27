package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityPerformanceResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long facilityId;
    private String facilityBusinessId;
    private String siteName;

    // Performance Data Fields 
    private Long targetPeriodId;
    private TargetPeriodType targetPeriodType;
    private Long performanceDataId;
    private LocalDate performanceDataSubmissionDate;
    private Integer performanceDataReportVersion;
    private PerformanceDataSubmissionType submissionType;
    private boolean processed;

    private BigDecimal targetPeriodBuyOutCost;
    private BuyOutSurplusCalculationDataContainer data;
    private BuyOutSurplusPaymentStatus paymentStatus;
    private boolean primaryRulesApplied;

    private boolean succeeded;
    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
