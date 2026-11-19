package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service.EmissionsTradingSchemeDataService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;

@ExtendWith(MockitoExtension.class)
public class EmissionsTradingSchemeDataSubmitInitializerTest {

    @InjectMocks
    private EmissionsTradingSchemeDataSubmitInitializer initializer;

    @Mock
    private EmissionsTradingSchemeDataService emissionsTradingSchemeDataService;

    @Test
    void initializePayload() {
        final Long facilityId = 22L;
        final String uketsId = "uketsId";
        final Year targetYear = Year.of(2026);
        final MeasurementType measurementType = MeasurementType.ENERGY_KWH;

        EmissionsTradingSchemeDataInputDetails emissionsTradingSchemeDataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
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
                        .build())
                .build();

        EmissionsTradingSchemeDataContainer container = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(measurementType)
                .uketsId(uketsId)
                .isUltraSmallEmitter(true)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                                .name(NATURAL_GAS.getDescription())
                                .fixedConversionFactorCode(NATURAL_GAS)
                                .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, MeasurementType.ENERGY_KWH))
                                .deliveredEnergy(BigDecimal.valueOf(5000000))
                                .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                                .primaryEnergy(BigDecimal.valueOf(5000000))
                                .build(),
                        PerformanceDataFacilityFuel.builder()
                                .name("Biofuel")
                                .conversionFactor(BigDecimal.valueOf(0.10046))
                                .deliveredEnergy(BigDecimal.valueOf(1500000))
                                .primaryConversionFactor(BigDecimal.ONE)
                                .primaryEnergy(BigDecimal.valueOf(1500000))
                                .build()
                ))
                .build();

        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(CcaResourceType.FACILITY)
                        .resourceId(facilityId.toString())
                        .build())
                )
                .payload(EmissionsTradingSchemeDataRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.EMISSIONS_TRADING_SCHEME_DATA_PAYLOAD)
                        .targetYear(targetYear)
                        .uketsId(uketsId)
                        .measurementType(measurementType)
                        .facilityId(facilityId)
                        .build())
                .build();

        EmissionsTradingSchemeDataSubmitRequestTaskPayload expected = EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT_PAYLOAD)
                .targetYear(targetYear)
                .uketsId(uketsId)
                .measurementType(measurementType)
                .emissionsTradingSchemeDataInputDetails(emissionsTradingSchemeDataInputDetails)
                .build();

        when(emissionsTradingSchemeDataService.getEmissionsTradingSchemeData(facilityId, targetYear)).thenReturn(Optional.of(container));

        // invoke
        RequestTaskPayload actual = initializer.initializePayload(request);

        // verify
        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.EMISSIONS_TRADING_SCHEME_DATA_SUBMIT);
    }
}
