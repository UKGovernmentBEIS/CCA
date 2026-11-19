package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmissionsTradingSchemeDataSubmitRequestTaskPayload extends RequestTaskPayload {

    private Long facilityId;

    private Year targetYear;

    private String uketsId;

    private MeasurementType measurementType;

    private EmissionsTradingSchemeDataInputDetails emissionsTradingSchemeDataInputDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();
}
