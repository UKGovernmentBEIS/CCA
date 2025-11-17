package uk.gov.cca.api.migration.cca3sectorassociation;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cca3SectorAssociationVO {

	@NotNull(message = "Row number must be a positive number")
	private Long rowNumber;

	@NotBlank
	private String sectorAcronym;

	private String subsectorName;

	@NotNull(message = "Invalid energy/carbon unit")
	private MeasurementType measurementType;

	@NotNull(message = "Must be a number smaller than 100 with up to 7 decimals")
	@DecimalMax(value = "100", message = "Must be a number smaller or equal than 100 ")
	@Digits(integer = 3, fraction = 7, message = "Must be a number with up to 7 decimals")
	private BigDecimal targetPeriod7Improvement;

	@NotNull(message = "Must be a number smaller than 100 with up to 7 decimals")
	@DecimalMax(value = "100", message = "Must be a number smaller or equal than 100 ")
	@Digits(integer = 3, fraction = 7, message = "Must be a number with up to 7 decimals")
	private BigDecimal targetPeriod8Improvement;

	@NotNull(message = "Must be a number smaller than 100 with up to 7 decimals")
	@DecimalMax(value = "100", message = "Must be a number smaller or equal than 100 ")
	@Digits(integer = 3, fraction = 7, message = "Must be a number with up to 7 decimals")
	private BigDecimal targetPeriod9Improvement;

	@NotNull(message = "Date must be in format DD/MM/YYYY")
	private LocalDate umaDate;

	@NotBlank(message = "The sector definition must be valid")
	private String sectorDefinition;
}
