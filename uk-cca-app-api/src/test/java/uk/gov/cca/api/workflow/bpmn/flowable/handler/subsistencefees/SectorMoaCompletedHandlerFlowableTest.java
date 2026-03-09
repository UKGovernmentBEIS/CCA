package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaCompletedService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SectorMoaCompletedHandlerFlowableTest {

	@InjectMocks
    private SectorMoaCompletedHandlerFlowable handler;

    @Mock
    private MoaCompletedService moaCompletedService;
    
    @Mock
    private RequestService requestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "request-id";
        final String moaRequestId = "moa-request-id";
        final long sectorId = 50L;
        final boolean sectorMoaSucceeded = true;
        final List<String> sectorMoaErrors = List.of();
        final Request request = Request.builder()
                .metadata(SectorMoaRequestMetadata.builder()
                        .parentRequestId(requestId)
                        .build())
                .build();

        when(requestService.findRequestById(moaRequestId)).thenReturn(request);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(moaRequestId);
        when(execution.getVariable(CcaBpmnProcessConstants.SECTOR_ID)).thenReturn(sectorId);
        when(execution.getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED))
        		.thenReturn(sectorMoaSucceeded);
        when(execution.getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ERRORS))
        		.thenReturn(sectorMoaErrors);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.SECTOR_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ERRORS);
        verify(moaCompletedService, times(1)).completed(requestId, sectorId, MoaType.SECTOR_MOA, moaRequestId, sectorMoaSucceeded, sectorMoaErrors, true);
    }
}
