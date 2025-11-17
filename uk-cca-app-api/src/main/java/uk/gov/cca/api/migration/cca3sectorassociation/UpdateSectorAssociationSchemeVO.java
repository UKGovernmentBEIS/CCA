package uk.gov.cca.api.migration.cca3sectorassociation;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class UpdateSectorAssociationSchemeVO {


	@NotNull
	private Long rowNumber;

	@NotBlank
	private String sectorAcronym;

	private String subsectorName;

	@NotNull
	private MeasurementType measurementType;

	@NotEmpty
	private Map<@NotBlank String, @NotNull BigDecimal> improvementTargetsByPeriod;

	@NotNull
	private LocalDate umaDate;

	@NotBlank
	private String sectorDefinition;
}
