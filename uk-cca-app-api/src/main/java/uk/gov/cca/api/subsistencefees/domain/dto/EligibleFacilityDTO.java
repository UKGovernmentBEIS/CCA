package uk.gov.cca.api.subsistencefees.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibleFacilityDTO {
    private Long id;
    private String facilityBusinessId;
    private String siteName;
    private String targetUnitBusinessId;
    private String operatorName;
    private Long accountId;
}
