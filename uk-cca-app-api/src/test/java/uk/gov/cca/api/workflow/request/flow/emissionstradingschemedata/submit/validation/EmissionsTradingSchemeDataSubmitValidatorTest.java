package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataSubmitValidatorTest {

    @InjectMocks
    private EmissionsTradingSchemeDataSubmitValidator submitValidator;

    @Mock
    private EmissionsTradingSchemeDataInputDataValidator inputDataValidator;

    @Mock
    private EmissionsTradingSchemeDataCalculatedDataValidator calculatedDataValidator;

    @Test
    void validate() {

        MeasurementType measurementType = MeasurementType.ENERGY_KWH;
        EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(false)
                .provideFuelDetails(null)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(2000000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(13500000))
                                .totalCo2Emissions(BigDecimal.valueOf(2776.3200000))
                                .build())
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder()
                        .emissionsTradingSchemeDataInputDetails(dataInputDetails)
                        .measurementType(measurementType)
                        .payloadType(CcaRequestTaskPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD)
                        .build())
                .build();

        BusinessValidationResult validationResult = BusinessValidationResult.builder()
                .violations(List.of())
                .valid(true)
                .build();

        when(inputDataValidator.validateInputData(dataInputDetails)).thenReturn(validationResult);
        when(calculatedDataValidator.validateInputCalculatedData(dataInputDetails.getEnergyFuelDetails(), measurementType)).thenReturn(List.of(validationResult));
        when(calculatedDataValidator.validateCalculatedResults(dataInputDetails.getEnergyFuelDetails(), measurementType)).thenReturn(List.of(validationResult));

        // invoke
        submitValidator.validate(requestTask);

        // verify
        verify(inputDataValidator, times(1)).validateInputData(dataInputDetails);
        verify(calculatedDataValidator, times(1)).validateInputCalculatedData(dataInputDetails.getEnergyFuelDetails(), measurementType);
        verify(calculatedDataValidator, times(1)).validateCalculatedResults(dataInputDetails.getEnergyFuelDetails(), measurementType);
    }

    @Test
    void validate_no_fuels_valid() {

        MeasurementType measurementType = MeasurementType.ENERGY_KWH;
        EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(false)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder()
                        .emissionsTradingSchemeDataInputDetails(dataInputDetails)
                        .measurementType(measurementType)
                        .payloadType(CcaRequestTaskPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD)
                        .build())
                .build();

        BusinessValidationResult validationResult = BusinessValidationResult.builder()
                .violations(List.of())
                .valid(true)
                .build();

        when(inputDataValidator.validateInputData(dataInputDetails)).thenReturn(validationResult);
        when(calculatedDataValidator.validateInputCalculatedData(dataInputDetails.getEnergyFuelDetails(), measurementType)).thenReturn(List.of(validationResult));
        when(calculatedDataValidator.validateCalculatedResults(dataInputDetails.getEnergyFuelDetails(), measurementType)).thenReturn(List.of(validationResult));

        // invoke
        submitValidator.validate(requestTask);

        // verify
        verify(inputDataValidator, times(1)).validateInputData(dataInputDetails);
        verify(calculatedDataValidator, times(1)).validateInputCalculatedData(dataInputDetails.getEnergyFuelDetails(), measurementType);
        verify(calculatedDataValidator, times(1)).validateCalculatedResults(dataInputDetails.getEnergyFuelDetails(), measurementType);
    }

    @Test
    void validate_invalid() {

        MeasurementType measurementType = MeasurementType.ENERGY_KWH;
        EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(false)
                .provideFuelDetails(null)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.LPG, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(2000000))
                                        .primaryEnergy(BigDecimal.valueOf(2000000))
                                        .build(),
                                PerformanceDataFacilityFixedConversionFactor.GAS_DIESEL_OIL, PerformanceDataFacilityFuelEnergyConsumption.builder()
                                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                                        .primaryEnergy(BigDecimal.valueOf(5000000))
                                        .build()
                        ))
                        .nonStandardFuels(List.of(
                                PerformanceDataFacilityNonStandardFuel.builder()
                                        .name("Biofuel")
                                        .deliveredEnergy(BigDecimal.valueOf(1500000))
                                        .primaryEnergy(BigDecimal.valueOf(1500000))
                                        .conversionFactor(BigDecimal.valueOf(0.10046))
                                        .build()
                        ))
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(13500000))
                                .totalCo2Emissions(BigDecimal.valueOf(2776.3200000))
                                .build())
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder()
                .payload(EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder()
                        .emissionsTradingSchemeDataInputDetails(dataInputDetails)
                        .measurementType(measurementType)
                        .payloadType(CcaRequestTaskPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD)
                        .build())
                .build();

        BusinessValidationResult validationResult = BusinessValidationResult.builder()
                .violations(List.of())
                .valid(false)
                .build();

        when(inputDataValidator.validateInputData(dataInputDetails)).thenReturn(validationResult);

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> submitValidator.validate(requestTask));

        // verify
        assertEquals(CcaErrorCode.INVALID_EMISSIONS_TRADING_SCHEME_DATA_SUBMIT, businessException.getErrorCode());
        verify(inputDataValidator, times(1)).validateInputData(dataInputDetails);
        verify(calculatedDataValidator, never()).validateInputCalculatedData(dataInputDetails.getEnergyFuelDetails(), measurementType);
        verify(calculatedDataValidator, never()).validateCalculatedResults(dataInputDetails.getEnergyFuelDetails(), measurementType);
    }
}
