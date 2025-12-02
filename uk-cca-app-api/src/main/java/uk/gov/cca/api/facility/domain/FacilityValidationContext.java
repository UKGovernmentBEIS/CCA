package uk.gov.cca.api.facility.domain;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityValidationContext {
	
	private Long id;

	private String facilityBusinessId;
	
	private LocalDate closedDate;

	Set<SchemeVersion> participatingSchemeVersions;

}
