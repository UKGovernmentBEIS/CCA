package uk.gov.cca.api.migration.sectorassociation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubSectorAssociationVO {
    
    private Long originalSubSectorId;
    private String name;
    
    private TargetSetVO targetSet;
}
