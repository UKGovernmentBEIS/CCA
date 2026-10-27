package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;

import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmissionsTradingSchemeDataRequestPayload extends CcaRequestPayload {

    private Year targetYear;
    private String uketsId;
    private MeasurementType measurementType;
}
