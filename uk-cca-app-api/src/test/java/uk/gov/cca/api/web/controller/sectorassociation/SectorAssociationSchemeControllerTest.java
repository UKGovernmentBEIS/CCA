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
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeDocumentService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.token.FileToken;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class SectorAssociationSchemeControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/sector-association/{sectorId}/scheme";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorAssociationSchemeController controller;

    @Mock
    private SectorAssociationSchemeService sectorAssociationSchemeService;

    @Mock
    private SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSectorAssociationSchemeBySectorAssociationIdWithoutTargetSet() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAssociationSchemeDTO sectorAssociationSchemeDTO = createSectorAssociationSchemeDTOWithoutTargetSet();

        when(sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId)).thenReturn(sectorAssociationSchemeDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH.replace("{sectorId}", sectorAssociationId.toString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sectorAssociationSchemeService, times(1)).getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);
    }

    @Test
    void getSectorAssociationSchemeBySectorAssociationIdWithoutSubsectors() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAssociationSchemeDTO sectorAssociationSchemeDTO = createSectorAssociationSchemeDTOWithoutSubsectors();

        when(sectorAssociationSchemeService.getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId)).thenReturn(sectorAssociationSchemeDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH.replace("{sectorId}", sectorAssociationId.toString()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sectorAssociationSchemeService, times(1)).getSectorAssociationSchemeBySectorAssociationId(sectorAssociationId);
    }


    @Test
    void generateGetSectorAssociationSchemeDocumentToken() throws Exception {
        Long sectorId = 1L;
        UUID documentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(sectorAssociationSchemeDocumentService.generateDocumentFileToken(sectorId, documentUuid)).thenReturn(expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH.replace("{sectorId}", sectorId.toString()) + "/document")
                        .param("documentUuid", documentUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(sectorAssociationSchemeDocumentService, times(1)).generateDocumentFileToken(sectorId, documentUuid);
    }

    private static SectorAssociationSchemeDTO createSectorAssociationSchemeDTOWithoutTargetSet() {
        return SectorAssociationSchemeDTO.builder()
                .umbrellaAgreement(SectorAssociationSchemeDocumentDTO.builder()
                        .id(1)
                        .uuid("test")
                        .fileName("umbrellaAgreement")
                        .fileType(".pdf")
                        .fileSize(1)
                        .build())
                .subsectorAssociationSchemes(Collections.singletonList(SubsectorAssociationSchemeInfoDTO.builder()
                        .subsectorAssociation(SubsectorAssociationDTO.builder()
                                .name("name")
                                .build())
                        .build()))
                .sectorDefinition("This is the sector definition")
                .umaDate(LocalDate.now())
                .build();
    }

    private static SectorAssociationSchemeDTO createSectorAssociationSchemeDTOWithoutSubsectors() {
        return SectorAssociationSchemeDTO.builder()
                .umbrellaAgreement(SectorAssociationSchemeDocumentDTO.builder()
                        .id(1)
                        .uuid("test")
                        .fileName("umbrellaAgreement")
                        .fileType(".pdf")
                        .fileSize(1)
                        .build())
                .targetSet(TargetSetDTO.builder()
                        .targetCurrencyType("Novem")
                        .energyOrCarbonUnit("kWh")
                        .targetCommitments(Collections.singletonList(TargetCommitmentDTO.builder()
                                .targetImprovement(BigDecimal.valueOf(19.000))
                                .targetPeriod("2013-2014")
                                .build()))
                        .build())
                .sectorDefinition("This is the sector definition")
                .umaDate(LocalDate.now())
                .build();
    }
}
