package uk.gov.cca.api.workflow.request.core.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAssociationDetails {

    private String subsectorAssociationName;
    private Map<SchemeVersion, SchemeData> schemeDataMap;
    
}
