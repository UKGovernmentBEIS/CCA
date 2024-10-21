package uk.gov.cca.api.migration.sectorassociation;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetSetVO {

    private String targetType;
    private String throughputUnit;
    private String energyCarbonUnit;

    private BigDecimal tp1SectorCommitment;
    private BigDecimal tp2SectorCommitment;
    private BigDecimal tp3SectorCommitment;
    private BigDecimal tp4SectorCommitment;
    private BigDecimal tp5SectorCommitment;
    private BigDecimal tp6SectorCommitment;

}
