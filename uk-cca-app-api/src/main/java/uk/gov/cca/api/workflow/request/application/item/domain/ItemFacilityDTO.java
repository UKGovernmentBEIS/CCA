package uk.gov.cca.api.workflow.request.application.item.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFacilityDTO {

	private Long facilityId;
	
	private String facilityBusinessId;
	
	private String siteName;
}
