package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;

import java.time.LocalDateTime;
import java.time.Year;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityPerformanceAccountTemplateProcessingRequestPayload extends CcaRequestPayload {

    private String parentRequestId;

    private SectorAssociationInfo sectorAssociationInfo;

    private LocalDateTime submissionDate;

    private Year targetYear;

    private FacilityDTO facility;
}
