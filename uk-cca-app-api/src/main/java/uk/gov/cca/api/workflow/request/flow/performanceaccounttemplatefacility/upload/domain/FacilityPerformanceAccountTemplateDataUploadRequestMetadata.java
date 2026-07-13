package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityPerformanceAccountTemplateDataUploadRequestMetadata extends RequestMetadata {

    private Year targetYear;

    private LocalDateTime submittedDate;
}
