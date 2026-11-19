package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service.EmissionsTradingSchemeDataService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmissionDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmittedRequestActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataSubmitServiceTest {

    @InjectMocks
    private EmissionsTradingSchemeDataSubmitService service;

    @Mock
    private EmissionsTradingSchemeDataService emissionsTradingSchemeDataService;

    @Mock
    private RequestService requestService;

    @Test
    void cancel() {
        final String assignee = "assignee";
        final AppUser appUser = AppUser.builder().userId(assignee).build();
        final Request request = Request.builder()
                .payload(EmissionsTradingSchemeDataRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder().request(request).build();

        // Invoke
        service.cancel(appUser, requestTask);

        // Verify
        verify(requestService, times(1))
                .addActionToRequest(request,
                        null,
                        CcaRequestActionType.EMISSIONS_TRADING_SCHEME_DATA_CANCELLED,
                        assignee);
    }

    @Test
    void applySave() {
        final Map<String, String> sectionsCompleted = Map.of("section1", "IN_PROGRESS");
        final Request request = Request.builder()
                .payload(EmissionsTradingSchemeDataRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder().build())
                .build();
        final EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
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
                                        .deliveredEnergy(BigDecimal.valueOf(500000))
                                        .primaryEnergy(BigDecimal.valueOf(500000))
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
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(2000000))
                                .build())
                        .build())
                .build();
        EmissionsTradingSchemeDataSaveRequestTaskActionPayload requestTaskActionPayload = EmissionsTradingSchemeDataSaveRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SAVE_PAYLOAD)
                .emissionsTradingSchemeDataInputDetails(dataInputDetails)
                .sectionsCompleted(sectionsCompleted)
                .build();

        // invoke
        service.applySave(requestTaskActionPayload, requestTask);

        // verify
        EmissionsTradingSchemeDataSubmitRequestTaskPayload requestTaskPayload = (EmissionsTradingSchemeDataSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(requestTaskPayload.getEmissionsTradingSchemeDataInputDetails()).isEqualTo(dataInputDetails);
        assertThat(requestTaskPayload.getSectionsCompleted()).isEqualTo(sectionsCompleted);
    }

    @Test
    void submit() {
        final String assignee = "assignee";
        final AppUser appUser = AppUser.builder().userId(assignee).build();
        final Long facilityId = 1L;
        final LocalDateTime submissionDate = LocalDate.of(2025, 3, 1).atStartOfDay();
        final LocalDateTime creationDate = LocalDate.of(2025, 1, 1).atStartOfDay();
        final Year targetYear = Year.of(2018);
        final boolean isUltraSmallEmitter = true;
        final String uketsId = "uketsId";
        final MeasurementType measurementType = MeasurementType.ENERGY_KWH;
        final int reportVersion = 2;

        final EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(isUltraSmallEmitter)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
                                NATURAL_GAS, PerformanceDataFacilityFuelEnergyConsumption.builder()
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
                .build();
        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder()
                        .targetYear(targetYear)
                        .measurementType(measurementType)
                        .uketsId(uketsId)
                        .facilityId(facilityId)
                        .emissionsTradingSchemeDataInputDetails(dataInputDetails)
                        .build())
                .build();
        EmissionsTradingSchemeDataContainer dataContainer = EmissionsTradingSchemeDataContainer.builder()
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

        final EmissionsTradingSchemeDataSubmittedRequestActionPayload actionPayload =
                EmissionsTradingSchemeDataSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED_PAYLOAD)
                        .details(EmissionsTradingSchemeDataSubmissionDetails.builder()
                                .targetPeriodYear(targetYear)
                                .submissionDate(submissionDate)
                                .creationDate(creationDate)
                                .build())
                        .performanceData(dataContainer)
                        .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                                .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                                .totalCo2Emissions(BigDecimal.valueOf(912.700))
                                .build())
                        .build();

        when(emissionsTradingSchemeDataService.submitEmissionsTradingSchemeData(dataContainer, facilityId, targetYear)).thenReturn(reportVersion);

        // Invoke
        service.submit(appUser, requestTask, submissionDate);

        // Verify

        assertThat(metadata.getSubmittedDate()).isEqualTo(submissionDate.toLocalDate());
        assertThat(requestPayload.getEtsDataInputDetails()).isEqualTo(dataInputDetails);
        assertThat(requestPayload.getMeasurementType()).isEqualTo(measurementType);
        assertThat(requestPayload.getUketsId()).isEqualTo(uketsId);
        verify(emissionsTradingSchemeDataService, times(1)).submitEmissionsTradingSchemeData(dataContainer, facilityId, targetYear);
        verify(requestService, times(1)).addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.EMISSIONS_TRADING_SCHEME_DATA_SUBMITTED,
                assignee);
    }
}
