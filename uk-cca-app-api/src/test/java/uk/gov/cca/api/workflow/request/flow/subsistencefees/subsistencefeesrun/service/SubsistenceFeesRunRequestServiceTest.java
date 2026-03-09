package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunRequestServiceTest {

	@InjectMocks
    private SubsistenceFeesRunRequestService service;

    @Mock
    private RequestService requestService;

    @Test
    void updateRequestMetadata() {
        String requestId = "requestId";
        Long sectorId = 1L;
        SubsistenceFeesRunRequestMetadata metadata = SubsistenceFeesRunRequestMetadata.builder()
                .chargingYear(Year.of(2025))
                .sectorsReports(new HashMap<>(Map.of(sectorId, MoaReport.builder().succeeded(true).build())))
                .skippedSectors(0L)
                .build();
        Request request = Request.builder().id("S2501").metadata(metadata).build();
        when(requestService.findRequestByIdForUpdate(requestId)).thenReturn(request);

        service.updateRequestMetadata(requestId, sectorId);

        verify(requestService, times(1)).findRequestByIdForUpdate(requestId);
        assertThat(metadata.getSectorsReports()).isEmpty();
        assertThat(metadata.getSkippedSectors()).isEqualTo(1L);
    }
    
    @Test
    void getNumberOfSectorsCompleted() {
    	String requestId = "1";
		Request request = Request.builder()
				.id(requestId)
				.metadata(SubsistenceFeesRunRequestMetadata.builder()
						.skippedSectors(5L)
						.sectorsReports(Map.of(
								1L, MoaReport.builder().succeeded(true).build(),
								2L, MoaReport.builder().succeeded(false).build(),
								3L, MoaReport.builder().succeeded(null).build()
								))
						.build())
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		
		long result = service.getNumberOfSectorsCompleted(requestId);
		
		assertThat(result).isEqualTo(7);
		
		verify(requestService, times(1)).findRequestById(requestId);
    }
    
    @Test
    void getNumberOfAccountsCompleted() {
    	String requestId = "1";
		Request request = Request.builder()
				.id(requestId)
				.metadata(SubsistenceFeesRunRequestMetadata.builder()
						.accountsReports(Map.of(
								1L, MoaReport.builder().succeeded(true).build(),
								2L, MoaReport.builder().succeeded(false).build(),
								3L, MoaReport.builder().succeeded(null).build()
								))
						.build())
				.build();
		
		when(requestService.findRequestById(requestId)).thenReturn(request);
		
		long result = service.getNumberOfAccountsCompleted(requestId);
		
		assertThat(result).isEqualTo(2);
		
		verify(requestService, times(1)).findRequestById(requestId);
    }
}
