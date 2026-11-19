package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityFuelEnergyConsumption;
import uk.gov.cca.api.workflow.request.flow.common.domain.PerformanceDataFacilityNonStandardFuel;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.domain.EmissionsTradingSchemeDataRequestPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataInputEnergyFuelDetails;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.service.EmissionsTradingSchemeDataSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataSaveActionHandlerTest {

    @InjectMocks
    private EmissionsTradingSchemeDataSaveActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmissionsTradingSchemeDataSubmitService emissionsTradingSchemeDataSubmitService;

    @Test
    void process() {
        final String assignee = "assignee";
        final AppUser appUser = AppUser.builder().userId(assignee).build();
        final Long requestTaskId = 1L;
        final Request request = Request.builder()
                .payload(EmissionsTradingSchemeDataRequestPayload.builder().build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .payload(EmissionsTradingSchemeDataSubmitRequestTaskPayload.builder().build())
                .build();
        final EmissionsTradingSchemeDataInputDetails dataInputDetails = EmissionsTradingSchemeDataInputDetails.builder()
                .isUltraSmallEmitter(true)
                .provideFuelDetails(true)
                .energyFuelDetails(EmissionsTradingSchemeDataInputEnergyFuelDetails.builder()
                        .standardFuels(Map.of(
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
                .sectionsCompleted(Map.of())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // invoke
        handler.process(requestTaskId, CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION, appUser, requestTaskActionPayload);

        // verify
        verify(emissionsTradingSchemeDataSubmitService, times(1)).applySave(requestTaskActionPayload, requestTask);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION);
    }
}
