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

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.dto.*;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationSchemeService;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubsectorAssociationViewControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/sector-association/{sectorId}/subsector-association/{subsectorId}/";

    private MockMvc mockMvc;

    @InjectMocks
    private SubsectorAssociationViewController controller;

    @Mock
    private SubsectorAssociationSchemeService subsectorAssociationSchemeService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSubsectorAssociationSchemeBySubsectorAssociationId() throws Exception {
        Long sectorAssociationId = 1L;
        Long subsectorAssociationd = 2L;
        SubsectorAssociationSchemesDTO subsectorAssociationSchemesDTO = SubsectorAssociationSchemesDTO.builder()
        		.subsectorAssociationSchemeMap(Map.of(SchemeVersion.CCA_2, createSubsectorAssociationSchemeDTO()))
        		.build(); 

        when(subsectorAssociationSchemeService.getSubsectorAssociationSchemesBySubsectorAssociationId(sectorAssociationId, subsectorAssociationd))
        		.thenReturn(subsectorAssociationSchemesDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH.replace("{sectorId}", sectorAssociationId.toString()).replace("{subsectorId}", subsectorAssociationd.toString()) + "scheme")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(subsectorAssociationSchemeService, times(1))
        		.getSubsectorAssociationSchemesBySubsectorAssociationId(sectorAssociationId, subsectorAssociationd);
    }

    private SubsectorAssociationSchemeDTO createSubsectorAssociationSchemeDTO() {
        return SubsectorAssociationSchemeDTO.builder()
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
