package uk.gov.cca.api.migration.underlyingagreement.baselinetargets;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetPeriod6DetailsVO {
    
    private String targetUnitId;
    
    // TargetComposition
    private String measurementType;
    private String agreementCompositionType;
    private Boolean isTargetUnitThroughputMeasured;
    private String throughputUnit;
    private BigDecimal conversionFactor;

    // BaselineData
    private Boolean estimatedData;
    private LocalDate baselineDate;
    private String explanation;
    private BigDecimal energy;
    private Boolean usedReportingMechanism;
    private BigDecimal throughput;
    private BigDecimal energyCarbonFactor;
    private BigDecimal performance;
    
    // Targets
    private BigDecimal improvement; // %
    private BigDecimal target;
    
    // Sector/SubSector data
    private String sectorAgreementCompositionType;
    private String sectorMeasurementType;
    private String sectorThroughputUnit;
    
    private boolean isTP6;
}
