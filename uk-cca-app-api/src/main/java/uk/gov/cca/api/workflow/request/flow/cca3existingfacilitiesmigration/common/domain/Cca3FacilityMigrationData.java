package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain;

import com.opencsv.bean.CsvCustomBindByPosition;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.common.converters.CsvBigDecimalConverter;
import uk.gov.cca.api.common.converters.CsvBooleanConverter;
import uk.gov.cca.api.common.converters.CsvLocalDateConverter;
import uk.gov.cca.api.common.converters.CsvMeasurementTypeConverter;
import uk.gov.cca.api.common.converters.CsvStringTrimConverter;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#baselineDate == null || !T(java.time.LocalDate).parse(#baselineDate).isBefore(T(java.time.LocalDate).of(2022, 1, 1))}",
        message = "cca3FacilityMigrationData.baselineDate")
@SpELExpression(expression = "{(#baselineDate != null && T(java.time.LocalDate).parse(#baselineDate) != T(java.time.LocalDate).of(2022, 1, 1)) == (#explanation != null)}",
        message = "cca3FacilityMigrationData.explanation")
@SpELExpression(expression = "{(T(java.lang.Boolean).FALSE.equals(#participatingInCca3Scheme) == (" +
        "#baselineDate == null && #measurementType == null && #energyCarbonFactor == null && #usedReportingMechanism == null " +
        "&& #tp7Improvement == null && #tp8Improvement == null && #tp9Improvement == null && #totalFixedEnergy == null " +
        "&& #totalVariableEnergy == null && #calculatorFileUuid == null && #calculatorFileName == null && #totalThroughput == null && #throughputUnit == null)) " +
        "&& (T(java.lang.Boolean).TRUE.equals(#participatingInCca3Scheme) == (" +
        "#baselineDate != null && #measurementType != null && #energyCarbonFactor != null && #usedReportingMechanism != null " +
        "&& #tp7Improvement != null && #tp8Improvement != null && #tp9Improvement != null && #totalFixedEnergy != null " +
        "&& #totalVariableEnergy != null && #calculatorFileUuid != null && #calculatorFileName != null && #totalThroughput != null && #throughputUnit != null)) " +
        "}",
        message = "cca3FacilityMigrationData.participatingInCca3Scheme")
public class Cca3FacilityMigrationData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // TU ID
    @CsvCustomBindByPosition(position = 0, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank
    @Size(max = 255)
    private String accountBusinessId;

    // Facility ID
    @CsvCustomBindByPosition(position = 1, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank
    @Size(max = 255)
    private String facilityBusinessId;

    // Facility Name
    @CsvCustomBindByPosition(position = 2, converter = CsvStringTrimConverter.class, required = true)
    @NotBlank
    @Size(max = 255)
    private String facilityName;

    // Will this facility participate in the CCA3 (2026-2030) scheme
    @CsvCustomBindByPosition(position = 3, converter = CsvBooleanConverter.class, required = true)
    @NotNull
    private Boolean participatingInCca3Scheme;

    // Baseline start date
    @CsvCustomBindByPosition(position = 4, converter = CsvLocalDateConverter.class)
    private LocalDate baselineDate;

    // Reason (if baseline start date later than 01/01/2022)
    @CsvCustomBindByPosition(position = 5, converter = CsvStringTrimConverter.class)
    @Size(max = 10000)
    private String explanation;

    // Baseline energy or carbon (unit)
    @CsvCustomBindByPosition(position = 6, converter = CsvMeasurementTypeConverter.class)
    private MeasurementType measurementType;

    // Baseline energy to carbon factor (kgC/kWh)
    @CsvCustomBindByPosition(position = 7, converter = CsvBigDecimalConverter.class)
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal energyCarbonFactor;

    // SRM used
    @CsvCustomBindByPosition(position = 8, converter = CsvBooleanConverter.class)
    private Boolean usedReportingMechanism;

    // TP7 improvement target
    @CsvCustomBindByPosition(position = 9, converter = CsvBigDecimalConverter.class)
    @DecimalMax(value = "100")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal tp7Improvement;

    // TP8 improvement target
    @CsvCustomBindByPosition(position = 10, converter = CsvBigDecimalConverter.class)
    @DecimalMax(value = "100")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal tp8Improvement;

    // TP9 improvement target
    @CsvCustomBindByPosition(position = 11, converter = CsvBigDecimalConverter.class)
    @DecimalMax(value = "100")
    @Digits(integer = 3, fraction = 7)
    private BigDecimal tp9Improvement;

    // Baseline total fixed energy (or carbon) value
    @CsvCustomBindByPosition(position = 12, converter = CsvBigDecimalConverter.class)
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalFixedEnergy;

    // Baseline total variable energy (or carbon)
    @CsvCustomBindByPosition(position = 13, converter = CsvBigDecimalConverter.class)
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalVariableEnergy;

    // Baseline Total Throughput
    @CsvCustomBindByPosition(position = 14, converter = CsvBigDecimalConverter.class)
    @Positive
    @Digits(integer = Integer.MAX_VALUE, fraction = 7)
    private BigDecimal totalThroughput;

    // Baseline throughput unit
    @CsvCustomBindByPosition(position = 15, converter = CsvStringTrimConverter.class)
    @Size(max = 255)
    private String throughputUnit;

    @Size(max = 255)
    private String calculatorFileUuid;

    @Size(max = 255)
    private String calculatorFileName;

    private boolean calculatorFileProvided;
}
