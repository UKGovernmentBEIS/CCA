package uk.gov.cca.api.workflow.request.flow.cca2termination.processing.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cca2TerminationAccountProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {

	private List<FacilityBaseInfoDTO> excludedFacilities;
}
