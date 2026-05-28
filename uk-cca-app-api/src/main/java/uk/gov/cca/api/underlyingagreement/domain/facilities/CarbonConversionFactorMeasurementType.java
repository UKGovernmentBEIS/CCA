package uk.gov.cca.api.underlyingagreement.domain.facilities;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarbonConversionFactorMeasurementType {
	
	KGCE_PER_KWH("kgCe/kWh"),
	KGCO2E_PER_KWH("kgCO2e/kWh");
	
	@JsonValue
	private final String description;
}
