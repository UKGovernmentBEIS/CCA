package uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowFacilityDTO {

    @NotNull
    @Size(max = 255)
    private String facilityBusinessId;

    @NotNull
    private Boolean isHistorical;
}
