package uk.gov.cca.api.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchemeData {
	
	private MeasurementType sectorMeasurementType;
    private String sectorThroughputUnit;
}
