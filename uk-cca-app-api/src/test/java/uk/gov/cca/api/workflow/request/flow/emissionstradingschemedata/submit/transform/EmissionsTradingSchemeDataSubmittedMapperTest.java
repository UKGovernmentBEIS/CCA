package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmittedRequestActionPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataSubmittedMapperTest {

    private final EmissionsTradingSchemeDataSubmittedMapper mapper = Mappers.getMapper(EmissionsTradingSchemeDataSubmittedMapper.class);

    @Test
    void toEmissionsTradingSchemeDataSubmittedRequestActionPayload() {
        final Long facilityId = 1L;
        final LocalDateTime submissionDate = LocalDate.of(2025, 3, 1).atStartOfDay();
        final LocalDateTime creationDate = LocalDate.of(2025, 1, 1).atStartOfDay();
        final Year targetYear = Year.of(2018);
        final boolean isUltraSmallEmitter = true;
        final String uketsId = "uketsId";
        final MeasurementType measurementType = MeasurementType.ENERGY_KWH;

        final EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(isUltraSmallEmitter)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build()
                        ))
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(912.700))
                                .build())
                        .build())
                .build();

        EmissionsTradingSchemeDataRequestPayload requestPayload = EmissionsTradingSchemeDataRequestPayload.builder()
                .targetYear(targetYear)
                .measurementType(measurementType)
                .uketsId(uketsId)
                .facilityId(facilityId)
                .etsDataInputDetails(dataInputDetails)
                .build();
        EmissionsTradingSchemeDataRequestMetadata metadata = EmissionsTradingSchemeDataRequestMetadata.builder().build();
        Request request = Request.builder()
                .payload(requestPayload)
                .metadata(metadata)
                .creationDate(creationDate)
                .submissionDate(submissionDate)
                .build();
        EmissionsTradingSchemeDataContainer performanceData = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(measurementType)
                .isUltraSmallEmitter(isUltraSmallEmitter)
                .uketsId(uketsId)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                        .name(NATURAL_GAS.getDescription())
                        .fixedConversionFactorCode(NATURAL_GAS)
                        .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, measurementType))
                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                        .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                        .primaryEnergy(BigDecimal.valueOf(5000000))
                        .build()))
                .build();

        final EmissionsTradingSchemeDataSubmittedRequestActionPayload expected =
                EmissionsTradingSchemeDataSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED_PAYLOAD)
                        .details(EmissionsTradingSchemeDataSubmissionDetails.builder()
                                .targetPeriodYear(targetYear)
                                .submissionDate(submissionDate)
                                .creationDate(creationDate)
                                .build())
                        .performanceData(performanceData)
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(912.700))
                                .build())
                        .build();

        // invoke
        EmissionsTradingSchemeDataSubmittedRequestActionPayload actual = mapper.toEmissionsTradingSchemeDataSubmittedRequestActionPayload(request, performanceData);

        // verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toEmissionsTradingSchemeDataSubmittedRequestActionPayload_empty_fuels() {
        final Long facilityId = 1L;
        final LocalDateTime submissionDate = LocalDate.of(2025, 3, 1).atStartOfDay();
        final LocalDateTime creationDate = LocalDate.of(2025, 1, 1).atStartOfDay();
        final Year targetYear = Year.of(2018);
        final boolean isUltraSmallEmitter = true;
        final String uketsId = "uketsId";
        final MeasurementType measurementType = MeasurementType.ENERGY_KWH;

        final EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(isUltraSmallEmitter)
                .provideFuelDetails(false)
                .build();

        EmissionsTradingSchemeDataRequestPayload requestPayload = EmissionsTradingSchemeDataRequestPayload.builder()
                .targetYear(targetYear)
                .measurementType(measurementType)
                .uketsId(uketsId)
                .facilityId(facilityId)
                .etsDataInputDetails(dataInputDetails)
                .build();
        EmissionsTradingSchemeDataRequestMetadata metadata = EmissionsTradingSchemeDataRequestMetadata.builder().build();
        Request request = Request.builder()
                .payload(requestPayload)
                .metadata(metadata)
                .creationDate(creationDate)
                .submissionDate(submissionDate)
                .build();
        EmissionsTradingSchemeDataContainer performanceData = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(measurementType)
                .isUltraSmallEmitter(isUltraSmallEmitter)
                .uketsId(uketsId)
                .build();

        final EmissionsTradingSchemeDataSubmittedRequestActionPayload expected =
                EmissionsTradingSchemeDataSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED_PAYLOAD)
                        .details(EmissionsTradingSchemeDataSubmissionDetails.builder()
                                .targetPeriodYear(targetYear)
                                .submissionDate(submissionDate)
                                .creationDate(creationDate)
                                .build())
                        .performanceData(performanceData)
                        .build();

        // invoke
        EmissionsTradingSchemeDataSubmittedRequestActionPayload actual = mapper.toEmissionsTradingSchemeDataSubmittedRequestActionPayload(request, performanceData);

        // verify
        assertThat(actual).isEqualTo(expected);
    }
}
