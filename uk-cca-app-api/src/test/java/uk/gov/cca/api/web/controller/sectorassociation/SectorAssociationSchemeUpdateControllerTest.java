package uk.gov.cca.api.web.controller.sectorassociation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDetailsUpdateDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentUpdateDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeUpdateService;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class SectorAssociationSchemeUpdateControllerTest {

    private static final String BASE_PATH = "/v1.0/sector-schemes/";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private SectorAssociationSchemeUpdateService sectorAssociationSchemeUpdateService;

    @InjectMocks
    private SectorAssociationSchemeUpdateController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void updateSectorAssociationSchemeDetails() throws Exception {
        Long sectorAssociationId = 1L;
        Long sectorAssociationSchemeId = 1L;
        SectorAssociationSchemeDetailsUpdateDTO sectorAssociationSchemeDetailsUpdateDTO = createSectorAssociationSchemeDetailsUpdateDTO();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(BASE_PATH + sectorAssociationSchemeId + "/details")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sectorAssociationSchemeDetailsUpdateDTO)))
                .andExpect(status().isNoContent());

        verify(sectorAssociationSchemeUpdateService, times(1))
                .updateSectorAssociationSchemeDetails(sectorAssociationSchemeId, sectorAssociationSchemeDetailsUpdateDTO);
    }

    @Test
    void updateSectorAssociationSchemeTargetCommitments() throws Exception {
        final Long sectorAssociationSchemeId = 1L;
        final TargetCommitmentsUpdateDTO sectorAssociationTargetCommitmentsUpdateDTO = TargetCommitmentsUpdateDTO.builder()
                .targetCommitments(List.of(
                        TargetCommitmentUpdateDTO.builder()
                                .id(1L)
                                .targetImprovement(BigDecimal.valueOf(58))
                                .build(),
                        TargetCommitmentUpdateDTO.builder()
                                .id(2L)
                                .targetImprovement(BigDecimal.valueOf(25))
                                .build()))
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(BASE_PATH + sectorAssociationSchemeId + "/target-commitments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(sectorAssociationTargetCommitmentsUpdateDTO)))
                .andExpect(status().isNoContent());

        verify(sectorAssociationSchemeUpdateService, times(1))
                .updateSectorAssociationSchemeTargetCommitments(sectorAssociationSchemeId, sectorAssociationTargetCommitmentsUpdateDTO);
    }

    private SectorAssociationSchemeDetailsUpdateDTO createSectorAssociationSchemeDetailsUpdateDTO() {
        final String newSectorDef = "new scope def";
        final String newUuid = "07bde92d-e259-4e38-adeb-1c23915a33ed";
        final LocalDate newUmaDate = LocalDate.of(2026, 2, 2);
        return SectorAssociationSchemeDetailsUpdateDTO.builder()
                .umbrellaAgreementUuid(newUuid)
                .sectorDefinition(newSectorDef)
                .umaDate(newUmaDate)
                .build();
    }
}
