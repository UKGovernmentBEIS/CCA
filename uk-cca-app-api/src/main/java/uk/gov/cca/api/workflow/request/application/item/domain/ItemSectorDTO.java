package uk.gov.cca.api.workflow.request.application.item.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSectorDTO {

	private Long sectorId;
	
	private String sectorAcronym;

    private String sectorName;
}
