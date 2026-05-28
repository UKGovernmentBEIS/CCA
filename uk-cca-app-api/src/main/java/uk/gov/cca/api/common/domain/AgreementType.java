package uk.gov.cca.api.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgreementType {

	ENVIRONMENTAL_PERMITTING_REGULATIONS("EPR"),
	ENERGY_INTENSIVE("Energy intensive");
	
	private final String description;
}
