package uk.gov.cca.api.web.controller.buyoutsurplus;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutCostUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class BuyOutSurplusCostControllerTest {

	@InjectMocks
    private BuyOutSurplusCostController controller;

    @Mock
    private TargetPeriodService targetPeriodService;
    
	private static final String REQUEST_PATH = "/v1.0/buy-out-surplus/cost";

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    void getBuyOutSurplusCosts() throws Exception {
        final TargetPeriodBuyOutDetailsDTO dto = TargetPeriodBuyOutDetailsDTO.builder()
                .id(1L)
                .buyOutCost(25)
                .build();

        when(targetPeriodService.getTargetPeriodBuyOutDetailsBySchemeVersion(SchemeVersion.CCA_3))
                .thenReturn(List.of(dto));

        mockMvc.perform(get(REQUEST_PATH)
                        .param("schemeVersion", SchemeVersion.CCA_3.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].buyOutCost").value(25));

        verify(targetPeriodService).getTargetPeriodBuyOutDetailsBySchemeVersion(SchemeVersion.CCA_3);
    }
    
    @Test
    void updateBuyOutSurplusCost() throws Exception {
        TargetPeriodBuyOutCostUpdateDTO dto = TargetPeriodBuyOutCostUpdateDTO.builder()
                .buyOutCost(25)
                .build();

        mockMvc.perform(patch(REQUEST_PATH + "/{tp}", TargetPeriodType.TP7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());

        verify(targetPeriodService).updateBuyOutCost(TargetPeriodType.TP7, dto);
    }
    
    @Test
    void updateBuyOutSurplusCost_invalidBuyOutCost() throws Exception {
        TargetPeriodBuyOutCostUpdateDTO dto = TargetPeriodBuyOutCostUpdateDTO.builder()
                .buyOutCost(0)
                .build();

        mockMvc.perform(patch(REQUEST_PATH + "/{tp}", TargetPeriodType.TP7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(targetPeriodService);
    }
}
