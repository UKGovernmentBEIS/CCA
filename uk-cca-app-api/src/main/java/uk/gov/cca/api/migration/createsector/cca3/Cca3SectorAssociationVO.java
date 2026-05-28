package uk.gov.cca.api.migration.createsector.cca3;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.opencsv.bean.CsvCustomBindByPosition;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.converters.CsvLocalDateConverter;
import uk.gov.cca.api.common.converters.CsvMeasurementTypeConverter;
import uk.gov.cca.api.common.converters.CsvAgreementTypeConverter;
import uk.gov.cca.api.common.converters.CsvBigDecimalConverter;
import uk.gov.cca.api.common.converters.CsvStringTrimConverter;
import uk.gov.cca.api.common.domain.AgreementType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.migration.createsector.common.SectorAssociationSource;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cca3SectorAssociationVO implements SectorAssociationSource {

	// Sector details
	@CsvCustomBindByPosition(position = 0, converter = CsvStringTrimConverter.class, required = true)
	@NotBlank
    private String acronym;
	@CsvCustomBindByPosition(position = 1, converter = CsvStringTrimConverter.class, required = true)
	@NotBlank(message = "The sector name must be valid")
    private String commonName;
	@CsvCustomBindByPosition(position = 2, converter = CsvStringTrimConverter.class, required = true)
	@NotBlank(message = "The sector/trade association name must be valid")
    private String legalName;
	@CsvCustomBindByPosition(position = 3, converter = CsvAgreementTypeConverter.class, required = true)
	@NotNull(message = "Invalid EPR/energy intensive")
    private AgreementType energyIntensiveOrEPR;

	// Address
    @CsvCustomBindByPosition(position = 4, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The address line 1 must be valid")
    private String line1;
    @CsvCustomBindByPosition(position = 5, converter = CsvStringTrimConverter.class)
    private String line2;
    @CsvCustomBindByPosition(position = 6, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The city must be valid")
    private String city;
    @CsvCustomBindByPosition(position = 7, converter = CsvStringTrimConverter.class)
    private String county;
    @CsvCustomBindByPosition(position = 8, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The postcode must be valid")
    private String postcode;

    // Main Contact
    @CsvCustomBindByPosition(position = 9, converter = CsvStringTrimConverter.class)
    private String sectorContactTitle;
    @CsvCustomBindByPosition(position = 10, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The sector contact first name must be valid")
    private String sectorContactFirstName;
    @CsvCustomBindByPosition(position = 11, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The sector contact last name must be valid")
    private String sectorContactLastName;
    @CsvCustomBindByPosition(position = 12, converter = CsvStringTrimConverter.class)
    private String sectorContactJobTitle;
    @CsvCustomBindByPosition(position = 13, converter = CsvStringTrimConverter.class)
    private String sectorContactOrganisationName;
    @CsvCustomBindByPosition(position = 14, converter = CsvStringTrimConverter.class)
    private String sectorContactPhoneNumber;
    @CsvCustomBindByPosition(position = 15, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The sector contact email must be valid")
    private String sectorContactEmail;
    @CsvCustomBindByPosition(position = 16, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The sector contact address line 1 must be valid")
    private String sectorContactAddressLine1;
    @CsvCustomBindByPosition(position = 17, converter = CsvStringTrimConverter.class)
    private String sectorContactAddressLine2;
    @CsvCustomBindByPosition(position = 18, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The sector contact city must be valid")
    private String sectorContactAddressCity;
    @CsvCustomBindByPosition(position = 19, converter = CsvStringTrimConverter.class)
    private String sectorContactAddressCounty;
    @CsvCustomBindByPosition(position = 20, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The sector contact postcode must be valid")
    private String sectorContactAddressPostcode;

    // Scheme
    @CsvCustomBindByPosition(position = 21, converter = CsvLocalDateConverter.class, required = true)
    @NotNull(message = "Date must be in format DD/MM/YYYY")
    private LocalDate umaDate;
    @CsvCustomBindByPosition(position = 22, converter = CsvMeasurementTypeConverter.class, required = true)
    @NotNull(message = "Invalid energy/carbon unit")
    private MeasurementType energyCarbonUnit;
    @CsvCustomBindByPosition(position = 23, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank(message = "The sector definition must be valid")
    private String sectorDefinition;
    @CsvCustomBindByPosition(position = 24, converter = CsvBigDecimalConverter.class, required = true)
	@NotNull(message = "Must be a number smaller than 100 with up to 3 decimals")
	@DecimalMax(value = "99.999", message = "Must be a number smaller than 100 ")
	@Digits(integer = 2, fraction = 3, message = "Must be a number with up to 3 decimals")
    private BigDecimal tp7SectorCommitment;
    @CsvCustomBindByPosition(position = 25, converter = CsvBigDecimalConverter.class, required = true)
	@NotNull(message = "Must be a number smaller than 100 with up to 3 decimals")
	@DecimalMax(value = "99.999", message = "Must be a number smaller than 100 ")
	@Digits(integer = 2, fraction = 3, message = "Must be a number with up to 3 decimals")
    private BigDecimal tp8SectorCommitment;
    @CsvCustomBindByPosition(position = 26, converter = CsvBigDecimalConverter.class, required = true)
	@NotNull(message = "Must be a number smaller than 100 with up to 3 decimals")
	@DecimalMax(value = "99.999", message = "Must be a number smaller than 100 ")
	@Digits(integer = 2, fraction = 3, message = "Must be a number with up to 3 decimals")
    private BigDecimal tp9SectorCommitment;
}
