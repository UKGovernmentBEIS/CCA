package uk.gov.cca.api.migration.cca3sectorassociation;

import com.opencsv.bean.CsvCustomBindByPosition;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.converters.CsvBigDecimalConverter;
import uk.gov.cca.api.common.converters.CsvLocalDateConverter;
import uk.gov.cca.api.common.converters.CsvMeasurementTypeConverter;
import uk.gov.cca.api.common.converters.CsvStringTrimConverter;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cca3SectorAssociationVO {

	@CsvCustomBindByPosition(position = 0, converter = CsvStringTrimConverter.class, required = true)
	@NotBlank
	private String sectorAcronym;

	@CsvCustomBindByPosition(position = 1, converter = CsvStringTrimConverter.class)
	private String subsectorName;

	@CsvCustomBindByPosition(position = 2, converter = CsvMeasurementTypeConverter.class, required = true)
	@NotNull(message = "Invalid energy/carbon unit")
	private MeasurementType measurementType;

	@CsvCustomBindByPosition(position = 3, converter = CsvBigDecimalConverter.class, required = true)
	@NotNull(message = "Must be a number smaller than 100 with up to 7 decimals")
	@DecimalMax(value = "100", message = "Must be a number smaller or equal than 100 ")
	@Digits(integer = 3, fraction = 7, message = "Must be a number with up to 7 decimals")
	private BigDecimal targetPeriod7Improvement;

	@CsvCustomBindByPosition(position = 4, converter = CsvBigDecimalConverter.class, required = true)
	@NotNull(message = "Must be a number smaller than 100 with up to 7 decimals")
	@DecimalMax(value = "100", message = "Must be a number smaller or equal than 100 ")
	@Digits(integer = 3, fraction = 7, message = "Must be a number with up to 7 decimals")
	private BigDecimal targetPeriod8Improvement;

	@CsvCustomBindByPosition(position = 5, converter = CsvBigDecimalConverter.class, required = true)
	@NotNull(message = "Must be a number smaller than 100 with up to 7 decimals")
	@DecimalMax(value = "100", message = "Must be a number smaller or equal than 100 ")
	@Digits(integer = 3, fraction = 7, message = "Must be a number with up to 7 decimals")
	private BigDecimal targetPeriod9Improvement;

	@CsvCustomBindByPosition(position = 6, converter = CsvLocalDateConverter.class, required = true)
	@NotNull(message = "Date must be in format DD/MM/YYYY")
	private LocalDate umaDate;

	@CsvCustomBindByPosition(position = 7, converter = CsvStringTrimConverter.class, required = true)
	@NotBlank(message = "The sector definition must be valid")
	private String sectorDefinition;
}
