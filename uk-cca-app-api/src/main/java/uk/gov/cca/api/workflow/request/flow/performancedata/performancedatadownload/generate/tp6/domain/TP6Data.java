package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TP6Data {

    // Section 1
    private String sector;
    private String targetPeriod;
    private String reportVersion;

    // Section 2
    private String targetUnitId;
    private String operatorName;
    private int numOfFacilities;
    private String targetType;
    private String measurementUnit;
    private String throughputUnit;
    private LocalDate baselineDate;
    private BigDecimal baselineEnergy;
    private BigDecimal baselineThroughput;
    private BigDecimal improvement;
    private int bankedSurplusFromPreviousTP;

    // Section 5
    private BigDecimal previousBuyOutAfterSurplus;
    private BigDecimal previousSurplusUsed;
    private BigDecimal surplusGainedInTP;
}

