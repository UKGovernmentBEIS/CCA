package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service.SectorMoaCreateRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunTriggerSectorMoasHandlerTest {

	@InjectMocks
    private SubsistenceFeesRunTriggerSectorMoasHandler handler;
	
	@Mock
	private SectorMoaCreateRequestService sectorMoaCreateRequestService;
	
	@Mock
	private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
	
	@Mock
	private RequestService requestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_sector_is_eligible() throws Exception {
        final String requestId = "request-id";
        final Long sectorId = 50L;
        final Year chargingYear = Year.of(2025);
        final String requestBusinessKey = "requestBusinessKey";
        final Request request = Request.builder()
                .metadata(SubsistenceFeesRunRequestMetadata.builder()
                        .chargingYear(chargingYear)
                        .sectorsReports(Map.of(sectorId, MoaReport.builder().moaType(MoaType.SECTOR_MOA).build()))
                        .build())
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID)).thenReturn(sectorId);
        when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(requestBusinessKey);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(subsistenceFeesRunQueryService.isSectorEligibleForSubsistenceFeesRun(sectorId, chargingYear)).thenReturn(true);

        handler.execute(execution);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.SECTOR_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(subsistenceFeesRunQueryService, times(1)).isSectorEligibleForSubsistenceFeesRun(sectorId, chargingYear);
        verify(sectorMoaCreateRequestService, times(1)).createRequest(sectorId, requestId, requestBusinessKey);
        verify(execution, never()).setVariable(eq(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED), any());
        assertThat(((SubsistenceFeesRunRequestMetadata) request.getMetadata()).getSectorsReports()).hasSize(1);
    }

    @Test
    void execute_sector_is_not_eligible() throws Exception {
        final String requestId = "request-id";
        final Long sectorId = 50L;
        final String requestBusinessKey = "requestBusinessKey";
        final Year chargingYear = Year.of(2025);
        Map<Long, MoaReport> sectorReports = new HashMap<Long, MoaReport>() {{
            put(sectorId, MoaReport.builder().moaType(MoaType.SECTOR_MOA).build());
        }};
        Request request = Request.builder()
                .metadata(SubsistenceFeesRunRequestMetadata.builder()
                        .chargingYear(Year.of(2025))
                        .sectorsReports(sectorReports)
                        .build())
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID)).thenReturn(sectorId);
        when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(requestBusinessKey);
        when(execution.getVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED)).thenReturn(10);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(subsistenceFeesRunQueryService.isSectorEligibleForSubsistenceFeesRun(sectorId, chargingYear)).thenReturn(false);

        handler.execute(execution);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.SECTOR_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(subsistenceFeesRunQueryService, times(1)).isSectorEligibleForSubsistenceFeesRun(sectorId, chargingYear);
        verify(sectorMoaCreateRequestService, never()).createRequest(sectorId, requestId, requestBusinessKey);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED, 11);
        assertThat(((SubsistenceFeesRunRequestMetadata) request.getMetadata()).getSectorsReports()).isEmpty();
    }
}
