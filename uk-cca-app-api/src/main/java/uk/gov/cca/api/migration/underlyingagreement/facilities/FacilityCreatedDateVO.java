package uk.gov.cca.api.migration.underlyingagreement.facilities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityCreatedDateVO {

    //Account
    private String targetUnitId;
    
    private String facilityId;
    private LocalDateTime createdDate;
}
