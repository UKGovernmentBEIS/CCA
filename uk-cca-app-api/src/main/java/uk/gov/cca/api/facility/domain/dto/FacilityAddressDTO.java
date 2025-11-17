package uk.gov.cca.api.facility.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.referencedata.service.UKCountry;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityAddressDTO {

	@NotBlank
	@Size(max = 255)
	private String line1;

	@Size(max = 255)
	private String line2;

	@NotBlank
	@Size(max = 255)
	private String city;

	@NotBlank
	@Size(max = 255)
	private String postcode;

	@Size(max = 255)
	private String county;

	@NotBlank
	@Size(max = 255)
	@UKCountry
	private String country;
}
