package uk.gov.cca.api.sectorassociation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectorAssociationSchemeInfo {

    private TargetSetInfo targetSet;
    
    private LocalDate umaDate;
    
    private String sectorDefinition;
}
