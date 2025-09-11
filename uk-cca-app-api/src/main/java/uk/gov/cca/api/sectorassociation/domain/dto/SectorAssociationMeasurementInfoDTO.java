package uk.gov.cca.api.sectorassociation.domain.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorAssociationMeasurementInfoDTO {

    private String subsectorAssociationName;
    private Map<SchemeVersion, SchemeData> schemeDataMap;
}
