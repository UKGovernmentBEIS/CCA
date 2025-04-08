package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSubsistenceFeesServiceOrchestratorTest {

	@Mock
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    @InjectMocks
    private SectorAssociationSubsistenceFeesServiceOrchestrator orchestrator;
    
    @Test
    void getSectorSubsistenceFeesMoas() {
    	final long sectorAssociationId = 1L;
        final long page = 0;
        final long pageSize = 30;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
        		.paging(pagingRequest)
        		.moaType(MoaType.SECTOR_MOA)
        		.build();
        
        // Invoke
        orchestrator.getSectorSubsistenceFeesMoas(sectorAssociationId, criteria);

        // Verify
        verify(subsistenceFeesMoaQueryService, times(1)).getSectorSubsistenceFeesMoas(sectorAssociationId, criteria);
    }
}
