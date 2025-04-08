package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class MoaCompletedServiceTest {

	@InjectMocks
    private MoaCompletedService moaCompletedService;

	@Mock
	private WorkflowService workflowService;
	
	@Mock
	private RequestService requestService;

    @Test
    void completed_succeeded() {
    	final String requestId = "request-id";
    	final String moaRequestId = "moa-request-id";
    	final Long sectorId = 50L;
    	final Request request = Request.builder()
    			.metadata(SubsistenceFeesRunRequestMetadata.builder()
    					.chargingYear(Year.of(2025))
    					.sectorsReports(Map.of(sectorId, MoaReport.builder().moaType(MoaType.SECTOR_MOA).build()))
    					.build())
    			.build();
    	
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	
    	moaCompletedService.completed(requestId, sectorId, MoaType.SECTOR_MOA, moaRequestId, true, List.of());
        verify(requestService, times(1)).findRequestById(requestId);
        assertThat(((SubsistenceFeesRunRequestMetadata)request.getMetadata()).getSectorsReports().get(sectorId).getSucceeded()).isTrue();
        assertThat(((SubsistenceFeesRunRequestMetadata)request.getMetadata()).getSectorsReports().get(sectorId).getIssueDate()).isNotNull();
    }
    
    @Test
    void completed_failed() {
    	final String requestId = "request-id";
    	final String moaRequestId = "moa-request-id";
    	final Long accountId = 50L;
    	final Request request = Request.builder()
    			.metadata(SubsistenceFeesRunRequestMetadata.builder()
    					.chargingYear(Year.of(2025))
    					.accountsReports(Map.of(accountId, MoaReport.builder().moaType(MoaType.TARGET_UNIT_MOA).build()))
    					.build())
    			.build();
    	
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(requestService.findRequestById(moaRequestId)).thenReturn(Request.builder().processInstanceId("123").build());
    	
    	moaCompletedService.completed(requestId, accountId, MoaType.TARGET_UNIT_MOA, moaRequestId, false, List.of("errors"));
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).findRequestById(moaRequestId);
        verify(workflowService, times(1)).deleteProcessInstance("123", "Moa request failed");
        assertThat(((SubsistenceFeesRunRequestMetadata)request.getMetadata()).getAccountsReports().get(accountId).getSucceeded()).isFalse();
        assertThat(((SubsistenceFeesRunRequestMetadata)request.getMetadata()).getAccountsReports().get(accountId).getIssueDate()).isNull();
        assertThat(((SubsistenceFeesRunRequestMetadata)request.getMetadata()).getAccountsReports().get(accountId).getErrors()).isEqualTo(List.of("errors"));
    }
}
