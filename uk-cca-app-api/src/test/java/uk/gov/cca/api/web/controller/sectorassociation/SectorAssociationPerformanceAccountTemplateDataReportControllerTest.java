package uk.gov.cca.api.web.controller.sectorassociation;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

@ExtendWith(MockitoExtension.class)
class SectorAssociationPerformanceAccountTemplateDataReportControllerTest {

	private static final String CONTROLLER_PATH = "/v1.0/sector-association/{sectorAssociationId}/performance-account-template-data-report";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorAssociationPerformanceAccountTemplateDataReportController cut;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private PerformanceAccountTemplateDataQueryService dataQueryService;

    private ObjectMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setObjectMapper(mapper);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(cut);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        cut = (SectorAssociationPerformanceAccountTemplateDataReportController) aopProxy.getProxy();
                
        mockMvc = MockMvcBuilders.standaloneSetup(cut)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }
    
    @Test
    void getSectorPerformanceAccountTemplateDataReportList() throws Exception {
        Long sectorAssociationId = 1L;
        
        SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.targetPeriodType(TargetPeriodType.TP6)
				.build();
        
        SectorPerformanceAccountTemplateDataReportListDTO listDTO = SectorPerformanceAccountTemplateDataReportListDTO.builder()
				.items(List.of(SectorPerformanceAccountTemplateDataReportItemDTO.builder()
						.accountId(1L)
						.targetUnitAccountBusinessId("targUBID")
						.operatorName("opName")
						.build()))
				.total(1L)
				.build();
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(dataQueryService.getSectorPerformanceAccountTemplateDataReportListDTO(sectorAssociationId, criteria)).thenReturn(listDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.replace("{sectorAssociationId}", sectorAssociationId.toString()))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                		.andExpect(status().isOk())
                		.andExpect(jsonPath("$.total").value(listDTO.getTotal()))
                        .andExpect(jsonPath("$.items[0].accountId").value(listDTO.getItems().get(0).getAccountId()))
                        .andExpect(jsonPath("$.items[0].targetUnitAccountBusinessId").value(listDTO.getItems().get(0).getTargetUnitAccountBusinessId()))
                        .andExpect(jsonPath("$.items[0].operatorName").value(listDTO.getItems().get(0).getOperatorName()));

        verify(dataQueryService, times(1)).getSectorPerformanceAccountTemplateDataReportListDTO(sectorAssociationId, criteria);
    }
    
    @Test
    void getSectorPerformanceAccountTemplateDataReportList_forbidden() throws Exception {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().userId("user").build();
        SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.targetPeriodType(TargetPeriodType.TP6)
				.build();
        
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getSectorPerformanceAccountTemplateDataReportList", String.valueOf(sectorAssociationId), null, null);
        
        mockMvc.perform(MockMvcRequestBuilders
                        .post(CONTROLLER_PATH.replace("{sectorAssociationId}", sectorAssociationId.toString()))
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
        				.andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(dataQueryService);
    }
    
}
