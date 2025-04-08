package uk.gov.cca.api.web.controller.targetperiodreporting.performancedata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountsPerformanceReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SectorAccountsPerformanceReportControllerTest {
	
	private static final String CONTROLLER_PATH = "/v1.0/target-period-reporting/sector-association";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorAccountsPerformanceReportController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQuerService;

    private ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
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

        controller = (SectorAccountsPerformanceReportController) aopProxy.getProxy();
                
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }
    
    @Test
    void getSectorAccountsPerformanceDataReport() throws Exception {
        Long sectorAssociationId = 1L;
        
        SectorAccountsPerformanceReportSearchCriteria criteria = SectorAccountsPerformanceReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.targetPeriodType(TargetPeriodType.TP6).build();
        
        SectorAccountsPerformanceReportItemDTO item = new SectorAccountsPerformanceReportItemDTO(1L, "ADS-T00001", "ADS operator", LocalDateTime.now(), 0, TargetPeriodResultType.OUTSTANDING, PerformanceDataSubmissionType.PRIMARY, false);

        SectorAccountsPerformanceReportDTO results = SectorAccountsPerformanceReportDTO.builder()
                .performanceReportItems(List.of(item))
                .total(1L)
                .build();

        when(accountPerformanceDataStatusQuerService.getSectorAccountsPerformanceDataReport(sectorAssociationId, criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.concat("/" + sectorAssociationId.toString() + "/accounts-performance-report"))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                		.andExpect(status().isOk())
                		.andExpect(jsonPath("$.total").value(results.getTotal()))
                        .andExpect(jsonPath("$.performanceReportItems[0].accountId").value(item.getAccountId()))
                        .andExpect(jsonPath("$.performanceReportItems[0].targetUnitBusinessId").value(item.getTargetUnitBusinessId()))
                        .andExpect(jsonPath("$.performanceReportItems[0].operatorName").value(item.getOperatorName()))
                        .andExpect(jsonPath("$.performanceReportItems[0].reportVersion").value(item.getReportVersion()));

        verify(accountPerformanceDataStatusQuerService, times(1)).getSectorAccountsPerformanceDataReport(sectorAssociationId, criteria);
    }
    
    @Test
    void getSectorAccountsPerformanceDataReport_forbidden() throws Exception {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().userId("user").build();
        SectorAccountsPerformanceReportSearchCriteria criteria = SectorAccountsPerformanceReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.targetPeriodType(TargetPeriodType.TP6).build();
        
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getSectorAccountsPerformanceDataReport", String.valueOf(sectorAssociationId), null, null);
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.concat("/" + sectorAssociationId.toString() + "/accounts-performance-report"))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(accountPerformanceDataStatusQuerService, never()).getSectorAccountsPerformanceDataReport(any(), any());
    }
    
    @Test
    void getSectorAccountsPerformanceDataReport_empty_targetPeriodType() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAccountsPerformanceReportSearchCriteria criteria = SectorAccountsPerformanceReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.build();
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.concat("/" + sectorAssociationId.toString() + "/accounts-performance-report"))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isBadRequest());

        verify(accountPerformanceDataStatusQuerService, never()).getSectorAccountsPerformanceDataReport(any(), any());
    }
    
    @Test
    void getSectorAccountsPerformanceDataReport_invalid_accountId() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAccountsPerformanceReportSearchCriteria criteria = SectorAccountsPerformanceReportSearchCriteria.builder()
        		.paging(PagingRequest.builder().pageNumber(0L).pageSize(30L).build())
        		.targetPeriodType(TargetPeriodType.TP6)
        		.targetUnitBusinessId("AI")
        		.build();
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.concat("/" + sectorAssociationId.toString() + "/accounts-performance-report"))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isBadRequest());

        verify(accountPerformanceDataStatusQuerService, never()).getSectorAccountsPerformanceDataReport(any(), any());
    }

}