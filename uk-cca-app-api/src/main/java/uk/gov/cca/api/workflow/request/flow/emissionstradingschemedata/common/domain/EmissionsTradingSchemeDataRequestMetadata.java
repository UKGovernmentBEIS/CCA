package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

import java.time.LocalDate;
import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmissionsTradingSchemeDataRequestMetadata extends RequestMetadata {

    private Year targetYear;
    private String uketsId;
    private LocalDate submittedDate;
}
