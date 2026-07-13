package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

import java.time.LocalDate;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityPerformanceAccountTemplateProcessingRequestMetadata extends RequestMetadata {

    private Integer reportVersion;
    private Year targetYear;
    private LocalDate submittedDate;
    private String uploadRequestId;
}
