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
import uk.gov.cca.api.sectorassociation.domain.dto.TargetCommitmentUpdateDTO;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationSchemeUpdateService;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
class SubsectorAssociationSchemeUpdateControllerTest {

    private static final String BASE_PATH = "/v1.0/subsector-schemes/";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private SubsectorAssociationSchemeUpdateService subsectorAssociationSchemeUpdateService;

    @InjectMocks
    private SubsectorAssociationSchemeUpdateController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void updateSubsectorAssociationSchemeTargetCommitments() throws Exception {
        final Long subsectorAssociationSchemeId = 1L;
        final TargetCommitmentsUpdateDTO subsectorAssociationTargetCommitmentsUpdateDTO = TargetCommitmentsUpdateDTO.builder()
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
                                .post(BASE_PATH + subsectorAssociationSchemeId + "/target-commitments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(subsectorAssociationTargetCommitmentsUpdateDTO)))
                .andExpect(status().isNoContent());

        verify(subsectorAssociationSchemeUpdateService, times(1))
                .updateSubsectorAssociationSchemeTargetCommitments(subsectorAssociationSchemeId, subsectorAssociationTargetCommitmentsUpdateDTO);
    }
}
