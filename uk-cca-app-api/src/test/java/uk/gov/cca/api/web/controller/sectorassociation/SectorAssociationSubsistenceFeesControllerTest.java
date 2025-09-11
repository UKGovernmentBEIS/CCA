package uk.gov.cca.api.web.controller.sectorassociation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.sectorassociation.service.SectorAssociationSubsistenceFeesServiceOrchestrator;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSubsistenceFeesControllerTest {

	private static final String CONTROLLER_PATH = "/v1.0/sector-association/";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorAssociationSubsistenceFeesController controller;

    @Mock
    private SectorAssociationSubsistenceFeesServiceOrchestrator orchestrator;
    
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSectorSubsistenceFeesMoas() throws Exception {
        final long sectorAssociationId = 1L;
        final int page = 0;
        final int pageSize = 30;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
        		.paging(pagingRequest)
        		.moaType(MoaType.SECTOR_MOA)
        		.build();
        SubsistenceFeesMoaSearchResultInfoDTO dto = 
        		new SubsistenceFeesMoaSearchResultInfoDTO(1L, "CCACM1200", null, null, null, null, null, null, null);
        SubsistenceFeesMoaSearchResults results = SubsistenceFeesMoaSearchResults.builder()
        		.subsistenceFeesMoas(List.of(dto))
        		.total(1L)
        		.build();
        
        when(orchestrator.getSectorSubsistenceFeesMoas(sectorAssociationId, criteria)).thenReturn(results);
        
        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + sectorAssociationId + "/subsistence-fees/moas")
        		.content(mapper.writeValueAsString(criteria))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.subsistenceFeesMoas[0].moaId").value(1L))
                .andExpect(jsonPath("$.subsistenceFeesMoas[0].transactionId").value("CCACM1200"));

        verify(orchestrator, times(1)).getSectorSubsistenceFeesMoas(sectorAssociationId, criteria);
    }
}
