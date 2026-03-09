package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service.SectorMoaCreateRequestService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunTriggerSectorMoasHandlerFlowableTest {

	@InjectMocks
    private SubsistenceFeesRunTriggerSectorMoasHandlerFlowable handler;
	
	@Mock
	private SectorMoaCreateRequestService sectorMoaCreateRequestService;
	
	@Mock
	private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
	
	@Mock
	private SubsistenceFeesRunRequestService subsistenceFeesRunRequestService;
	
	@Mock
	private RequestService requestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute_sector_is_eligible() {
        final String requestId = "request-id";
        final Long sectorId = 50L;
        final Year chargingYear = Year.of(2025);
        final String requestBusinessKey = "requestBusinessKey";
        final Request request = Request.builder()
                .metadata(SubsistenceFeesRunRequestMetadata.builder()
                        .chargingYear(chargingYear)
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
        verify(subsistenceFeesRunRequestService, never()).updateRequestMetadata(requestId, sectorId);
    }

    @Test
    void execute_sector_is_not_eligible() {
        final String requestId = "request-id";
        final Long sectorId = 50L;
        final String requestBusinessKey = "requestBusinessKey";
        final Year chargingYear = Year.of(2025);
        Request request = Request.builder()
                .metadata(SubsistenceFeesRunRequestMetadata.builder()
                        .chargingYear(Year.of(2025))
                        .build())
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID)).thenReturn(sectorId);
        when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(requestBusinessKey);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(subsistenceFeesRunQueryService.isSectorEligibleForSubsistenceFeesRun(sectorId, chargingYear)).thenReturn(false);

        handler.execute(execution);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.SECTOR_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(subsistenceFeesRunQueryService, times(1)).isSectorEligibleForSubsistenceFeesRun(sectorId, chargingYear);
        verify(subsistenceFeesRunRequestService, times(1)).updateRequestMetadata(requestId, sectorId);
        verify(sectorMoaCreateRequestService, never()).createRequest(sectorId, requestId, requestBusinessKey);
    }
}
