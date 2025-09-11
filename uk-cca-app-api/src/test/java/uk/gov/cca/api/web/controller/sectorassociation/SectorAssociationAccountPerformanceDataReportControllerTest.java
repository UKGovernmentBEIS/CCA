package uk.gov.cca.api.web.controller.sectorassociation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.sectorassociation.service.SectorAssociationAccountPerformanceDataReportServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

@ExtendWith(MockitoExtension.class)
class SectorAssociationAccountPerformanceDataReportControllerTest {
	
	private static final String CONTROLLER_PATH = "/v1.0/sector-association/{sectorAssociationId}/performance-data-report/";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorAssociationAccountPerformanceDataReportController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private SectorAssociationAccountPerformanceDataReportServiceOrchestrator orchestrator;

    private ObjectMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setObjectMapper(mapper);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (SectorAssociationAccountPerformanceDataReportController) aopProxy.getProxy();
                
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }
    
    @Test
    void getSectorAccountPerformanceDataReportList() throws Exception {
        Long sectorAssociationId = 1L;
        
        SectorAccountPerformanceDataReportSearchCriteria criteria = SectorAccountPerformanceDataReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
        		.targetPeriodType(TargetPeriodType.TP6).build();
        
        SectorAccountPerformanceDataReportItemDTO item = new SectorAccountPerformanceDataReportItemDTO(1L, "ADS-T00001", "ADS operator", LocalDateTime.now(), 0, TargetPeriodResultType.OUTSTANDING, PerformanceDataSubmissionType.PRIMARY, false);

        SectorAccountPerformanceDataReportListDTO results = SectorAccountPerformanceDataReportListDTO.builder()
                .performanceDataReportItems(List.of(item))
                .total(1L)
                .build();

        when(orchestrator.getSectorAccountPerformanceDataReportList(sectorAssociationId, criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.replace("{sectorAssociationId}", sectorAssociationId.toString()))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                		.andExpect(status().isOk())
                		.andExpect(jsonPath("$.total").value(results.getTotal()))
                        .andExpect(jsonPath("$.performanceDataReportItems[0].accountId").value(item.getAccountId()))
                        .andExpect(jsonPath("$.performanceDataReportItems[0].targetUnitAccountBusinessId").value(item.getTargetUnitAccountBusinessId()))
                        .andExpect(jsonPath("$.performanceDataReportItems[0].operatorName").value(item.getOperatorName()))
                        .andExpect(jsonPath("$.performanceDataReportItems[0].reportVersion").value(item.getReportVersion()));

        verify(orchestrator, times(1)).getSectorAccountPerformanceDataReportList(sectorAssociationId, criteria);
    }
    
    @Test
    void getSectorAccountPerformanceDataReportList_forbidden() throws Exception {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().userId("user").build();
        SectorAccountPerformanceDataReportSearchCriteria criteria = SectorAccountPerformanceDataReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
        		.targetPeriodType(TargetPeriodType.TP6).build();
        
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getSectorAccountPerformanceDataReportList", String.valueOf(sectorAssociationId), null, null);
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.replace("{sectorAssociationId}", sectorAssociationId.toString()))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(orchestrator, never()).getSectorAccountPerformanceDataReportList(any(), any());
    }
    
    @Test
    void getSectorAccountPerformanceDataReportList_empty_targetPeriodType() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAccountPerformanceDataReportSearchCriteria criteria = SectorAccountPerformanceDataReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
        		.build();
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.replace("{sectorAssociationId}", sectorAssociationId.toString()))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isBadRequest());

        verify(orchestrator, never()).getSectorAccountPerformanceDataReportList(any(), any());
    }
    
    @Test
    void getSectorAccountPerformanceDataReportList_invalid_accountId() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAccountPerformanceDataReportSearchCriteria criteria = SectorAccountPerformanceDataReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
        		.targetPeriodType(TargetPeriodType.TP6)
        		.targetUnitAccountBusinessId("AI")
        		.build();
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.replace("{sectorAssociationId}", sectorAssociationId.toString()))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isBadRequest());

        verify(orchestrator, never()).getSectorAccountPerformanceDataReportList(any(), any());
    }

}