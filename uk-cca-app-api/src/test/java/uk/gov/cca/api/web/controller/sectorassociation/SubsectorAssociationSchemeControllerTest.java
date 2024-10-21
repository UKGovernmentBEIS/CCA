package uk.gov.cca.api.web.controller.sectorassociation;

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
import uk.gov.cca.api.sectorassociation.domain.dto.*;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationSchemeService;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SubsectorAssociationSchemeControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/sector-association/{sectorId}/subsector-scheme/";

    private MockMvc mockMvc;

    @InjectMocks
    private SubsectorAssociationSchemeController controller;

    @Mock
    private SubsectorAssociationSchemeService subsectorAssociationSchemeService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSubsectorAssociationSchemeBySubsectorAssociationSchemeId() throws Exception {
        Long sectorAssociationId = 1L;
        Long subsectorAssociationSchemeId = 1L;
        SubsectorAssociationSchemeDTO subsectorAssociationSchemeDTO = createSubsectorAssociationSchemeDTO();

        when(subsectorAssociationSchemeService.getSubsectorAssociationSchemeBySubsectorAssociationSchemeId(sectorAssociationId)).thenReturn(subsectorAssociationSchemeDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH.replace("{sectorId}", sectorAssociationId.toString()) + subsectorAssociationSchemeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(subsectorAssociationSchemeService, times(1)).getSubsectorAssociationSchemeBySubsectorAssociationSchemeId(sectorAssociationId);
    }

    private SubsectorAssociationSchemeDTO createSubsectorAssociationSchemeDTO() {
        return SubsectorAssociationSchemeDTO.builder()
                .subsectorAssociation(SubsectorAssociationDTO.builder()
                        .name("name")
                        .build())
                .targetSet(TargetSetDTO.builder()
                        .targetCurrencyType("Novem")
                        .energyOrCarbonUnit("kWh")
                        .targetCommitments(Collections.singletonList(TargetCommitmentDTO.builder()
                                .targetImprovement(BigDecimal.valueOf(19.000))
                                .targetPeriod("2013-2014")
                                .build()))
                        .build())
                .build();
    }
}
