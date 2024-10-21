package uk.gov.cca.api.workflow.request.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.MeasurementType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAssociationDetails {

    private String subsectorAssociationName;
    private MeasurementType measurementType;
    private String throughputUnit;
}
