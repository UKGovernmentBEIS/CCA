package uk.gov.cca.api.sectorassociation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationMeasurementInfoDTO {

    private String subsectorAssociationName;
    private String measurementUnit;
    private String throughputUnit;
}
