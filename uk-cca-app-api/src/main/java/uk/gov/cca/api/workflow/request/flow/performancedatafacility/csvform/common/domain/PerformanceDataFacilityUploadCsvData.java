package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvCustomBindByPosition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.converters.CsvBigDecimalScale7HalfUpConverter;
import uk.gov.cca.api.common.converters.CsvBooleanConverter;
import uk.gov.cca.api.common.converters.CsvStringTrimConverter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceDataFacilityUploadCsvData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Facility ID
    @CsvCustomBindByPosition(position = 0, converter = CsvStringTrimConverter.class, required = true)
    private String facilityBusinessId;

    // Grid Electricity
    @CsvCustomBindByPosition(position = 1, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal gridElectricity;

    // Non-grid Electricity from renewables
    @CsvCustomBindByPosition(position = 2, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal nonGridElectricity;

    // Natural Gas
    @CsvCustomBindByPosition(position = 3, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal naturalGas;

    // LPG
    @CsvCustomBindByPosition(position = 4, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal lpg;

    // Gas Oil/ Diesel Oil
    @CsvCustomBindByPosition(position = 5, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal gasDieselOil;

    // Fuel Oil
    @CsvCustomBindByPosition(position = 6, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal fuelOil;

    // Kerosene
    @CsvCustomBindByPosition(position = 7, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal kerosene;

    // Coal
    @CsvCustomBindByPosition(position = 8, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal coal;

    // Coke
    @CsvCustomBindByPosition(position = 9, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal coke;

    // Petrol
    @CsvCustomBindByPosition(position = 10, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal petrol;

    // Nitrogen cooling
    @CsvCustomBindByPosition(position = 11, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal nitrogen;

    // Carbon dioxide cooling
    @CsvCustomBindByPosition(position = 12, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal carbonDioxide;

    // Ethane
    @CsvCustomBindByPosition(position = 13, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal ethane;

    // Naphtha
    @CsvCustomBindByPosition(position = 14, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal naphtha;

    // Petroleum Coke
    @CsvCustomBindByPosition(position = 15, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal petroleumCoke;

    // Refinery gas
    @CsvCustomBindByPosition(position = 16, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal refineryGas;

    // Other fuel name 1
    @CsvCustomBindByPosition(position = 17, converter = CsvStringTrimConverter.class)
    private String otherFuelName1;

    // Other fuel conversion factor 1
    @CsvCustomBindByPosition(position = 18, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor1;

    // Other fuel amount  1
    @CsvCustomBindByPosition(position = 19, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount1;

    // Other fuel name 2
    @CsvCustomBindByPosition(position = 20, converter = CsvStringTrimConverter.class)
    private String otherFuelName2;

    // Other fuel conversion factor 2
    @CsvCustomBindByPosition(position = 21, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor2;

    // Other fuel amount  2
    @CsvCustomBindByPosition(position = 22, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount2;

    // Other fuel name 3
    @CsvCustomBindByPosition(position = 23, converter = CsvStringTrimConverter.class)
    private String otherFuelName3;

    // Other fuel conversion factor 3
    @CsvCustomBindByPosition(position = 24, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor3;

    // Other fuel amount  3
    @CsvCustomBindByPosition(position = 25, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount3;

    // Other fuel name 4
    @CsvCustomBindByPosition(position = 26, converter = CsvStringTrimConverter.class)
    private String otherFuelName4;

    // Other fuel conversion factor 4
    @CsvCustomBindByPosition(position = 27, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor4;

    // Other fuel amount  4
    @CsvCustomBindByPosition(position = 28, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount4;

    // Other fuel name 5
    @CsvCustomBindByPosition(position = 29, converter = CsvStringTrimConverter.class)
    private String otherFuelName5;

    // Other fuel conversion factor 5
    @CsvCustomBindByPosition(position = 30, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor5;

    // Other fuel amount  5
    @CsvCustomBindByPosition(position = 31, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount5;

    // Other fuel name 6
    @CsvCustomBindByPosition(position = 32, converter = CsvStringTrimConverter.class)
    private String otherFuelName6;

    // Other fuel conversion factor 6
    @CsvCustomBindByPosition(position = 33, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor6;

    // Other fuel amount  6
    @CsvCustomBindByPosition(position = 34, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount6;

    // Other fuel name 7
    @CsvCustomBindByPosition(position = 35, converter = CsvStringTrimConverter.class)
    private String otherFuelName7;

    // Other fuel conversion factor 7
    @CsvCustomBindByPosition(position = 36, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor7;

    // Other fuel amount  7
    @CsvCustomBindByPosition(position = 37, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount7;

    // Other fuel name 8
    @CsvCustomBindByPosition(position = 38, converter = CsvStringTrimConverter.class)
    private String otherFuelName8;

    // Other fuel conversion factor 8
    @CsvCustomBindByPosition(position = 39, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor8;

    // Other fuel amount  8
    @CsvCustomBindByPosition(position = 40, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount8;

    // Other fuel name 9
    @CsvCustomBindByPosition(position = 41, converter = CsvStringTrimConverter.class)
    private String otherFuelName9;

    // Other fuel conversion factor 9
    @CsvCustomBindByPosition(position = 42, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor9;

    // Other fuel amount  9
    @CsvCustomBindByPosition(position = 43, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount9;

    // Other fuel name 10
    @CsvCustomBindByPosition(position = 44, converter = CsvStringTrimConverter.class)
    private String otherFuelName10;

    // Other fuel conversion factor 10
    @CsvCustomBindByPosition(position = 45, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelConversionFactor10;

    // Other fuel amount  10
    @CsvCustomBindByPosition(position = 46, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal otherFuelAmount10;

    // Confirm 70 percent
    @CsvCustomBindByPosition(position = 47, converter = CsvBooleanConverter.class)
    private Boolean atLeastSeventyPercentEnergyUsed;

    // Delivered CHP Electricity
    @CsvCustomBindByPosition(position = 48, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal electricitySuppliedFromCHP;

    // Actual throughput Fixed/totals only
    @CsvCustomBindByPosition(position = 49, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal actualThroughput;

    // Name Product 1
    @CsvCustomBindByPosition(position = 50, converter = CsvStringTrimConverter.class)
    private String productName1;

    // TP throughput Product 1
    @CsvCustomBindByPosition(position = 51, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput1;

    // Name Product 2
    @CsvCustomBindByPosition(position = 52, converter = CsvStringTrimConverter.class)
    private String productName2;

    // TP throughput Product 2
    @CsvCustomBindByPosition(position = 53, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput2;

    // Name Product 3
    @CsvCustomBindByPosition(position = 54, converter = CsvStringTrimConverter.class)
    private String productName3;

    // TP throughput Product 3
    @CsvCustomBindByPosition(position = 55, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput3;

    // Name Product 4
    @CsvCustomBindByPosition(position = 56, converter = CsvStringTrimConverter.class)
    private String productName4;

    // TP throughput Product 4
    @CsvCustomBindByPosition(position = 57, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput4;

    // Name Product 5
    @CsvCustomBindByPosition(position = 58, converter = CsvStringTrimConverter.class)
    private String productName5;

    // TP throughput Product 5
    @CsvCustomBindByPosition(position = 59, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput5;

    // Name Product 6
    @CsvCustomBindByPosition(position = 60, converter = CsvStringTrimConverter.class)
    private String productName6;

    // TP throughput Product 6
    @CsvCustomBindByPosition(position = 61, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput6;

    // Name Product 7
    @CsvCustomBindByPosition(position = 62, converter = CsvStringTrimConverter.class)
    private String productName7;

    // TP throughput Product 7
    @CsvCustomBindByPosition(position = 63, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput7;

    // Name Product 8
    @CsvCustomBindByPosition(position = 64, converter = CsvStringTrimConverter.class)
    private String productName8;

    // TP throughput Product 8
    @CsvCustomBindByPosition(position = 65, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput8;

    // Name Product 9
    @CsvCustomBindByPosition(position = 66, converter = CsvStringTrimConverter.class)
    private String productName9;

    // TP throughput Product 9
    @CsvCustomBindByPosition(position = 67, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput9;

    // Name Product 10
    @CsvCustomBindByPosition(position = 68, converter = CsvStringTrimConverter.class)
    private String productName10;

    // TP throughput Product 10
    @CsvCustomBindByPosition(position = 69, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput10;

    // Name Product 11
    @CsvCustomBindByPosition(position = 70, converter = CsvStringTrimConverter.class)
    private String productName11;

    // TP throughput Product 11
    @CsvCustomBindByPosition(position = 71, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput11;

    // Name Product 12
    @CsvCustomBindByPosition(position = 72, converter = CsvStringTrimConverter.class)
    private String productName12;

    // TP throughput Product 12
    @CsvCustomBindByPosition(position = 73, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput12;

    // Name Product 13
    @CsvCustomBindByPosition(position = 74, converter = CsvStringTrimConverter.class)
    private String productName13;

    // TP throughput Product 13
    @CsvCustomBindByPosition(position = 75, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput13;

    // Name Product 14
    @CsvCustomBindByPosition(position = 76, converter = CsvStringTrimConverter.class)
    private String productName14;

    // TP throughput Product 14
    @CsvCustomBindByPosition(position = 77, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput14;

    // Name Product 15
    @CsvCustomBindByPosition(position = 78, converter = CsvStringTrimConverter.class)
    private String productName15;

    // TP throughput Product 15
    @CsvCustomBindByPosition(position = 79, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput15;

    // Name Product 16
    @CsvCustomBindByPosition(position = 80, converter = CsvStringTrimConverter.class)
    private String productName16;

    // TP throughput Product 16
    @CsvCustomBindByPosition(position = 81, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput16;

    // Name Product 17
    @CsvCustomBindByPosition(position = 82, converter = CsvStringTrimConverter.class)
    private String productName17;

    // TP throughput Product 17
    @CsvCustomBindByPosition(position = 83, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput17;

    // Name Product 18
    @CsvCustomBindByPosition(position = 84, converter = CsvStringTrimConverter.class)
    private String productName18;

    // TP throughput Product 18
    @CsvCustomBindByPosition(position = 85, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput18;

    // Name Product 19
    @CsvCustomBindByPosition(position = 86, converter = CsvStringTrimConverter.class)
    private String productName19;

    // TP throughput Product 19
    @CsvCustomBindByPosition(position = 87, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput19;

    // Name Product 20
    @CsvCustomBindByPosition(position = 88, converter = CsvStringTrimConverter.class)
    private String productName20;

    // TP throughput Product 20
    @CsvCustomBindByPosition(position = 89, converter = CsvBigDecimalScale7HalfUpConverter.class)
    private BigDecimal productActualThroughput20;

    @JsonIgnore
    public List<String> getProductNames() {
        return Stream.of(productName1, productName2, productName3, productName4, productName5,
                        productName6, productName7, productName8, productName9, productName10,
                        productName11, productName12, productName13, productName14, productName15,
                        productName16, productName17, productName18, productName19, productName20
                ).filter(Objects::nonNull).toList();
    }
}
