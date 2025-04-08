package uk.gov.cca.api.subsistencefees.domain.dto.transform;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class SubsistenceFeesRunMoaDetailsInfo {

	private BigDecimal receivedAmount;
    
    private Long sectorMoasCount;
    
    private Long targetUnitMoasCount;
}
