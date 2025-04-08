package uk.gov.cca.api.migration.underlyingagreement.details;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementDetailsVO {
    private String targetUnitId;
    
    private Integer consolidationNumber;
    private LocalDateTime activationDate;
    
    // Sector/SubSector data
    private String sectorEnergyOrCarbonUnit;
    private String sectorThroughputUnit;
}
