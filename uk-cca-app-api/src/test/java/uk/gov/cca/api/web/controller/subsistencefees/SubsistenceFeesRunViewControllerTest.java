package uk.gov.cca.api.web.controller.subsistencefees;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.math.BigDecimal;
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
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesRunSearchResults;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunViewControllerTest {

	private static final String SUBSISTENCE_FEES_CONTROLLER_PATH = "/v1.0/subsistence-fees/runs/";

    private MockMvc mockMvc;

    @InjectMocks
    private SubsistenceFeesRunViewController subsistenceFeesRunViewController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;
    
    @Mock
    private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;
    
    @Mock
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;
    
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(subsistenceFeesRunViewController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        subsistenceFeesRunViewController = (SubsistenceFeesRunViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(subsistenceFeesRunViewController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }
    
    @Test
    void getSubsistenceFeesRuns() throws Exception {
        final AppUser user = AppUser.builder().roleType(REGULATOR).build();
        final int page = 0;
        final int pageSize = 30;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesRunSearchResultInfoDTO dto = new SubsistenceFeesRunSearchResultInfoDTO(1L, "S2501", null, null, null, null, null);
        SubsistenceFeesRunSearchResults results = SubsistenceFeesRunSearchResults.builder()
        		.subsistenceFeesRuns(List.of(dto))
        		.total(1L)
        		.build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(subsistenceFeesRunQueryService.getSubsistenceFeesRuns(user, pagingRequest)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.get(SUBSISTENCE_FEES_CONTROLLER_PATH)
        		.param("page", String.valueOf(pagingRequest.getPageNumber()))
                .param("size", String.valueOf(pagingRequest.getPageSize()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.subsistenceFeesRuns[0].paymentRequestId").value("S2501"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(subsistenceFeesRunQueryService, times(1)).getSubsistenceFeesRuns(user, pagingRequest);
    }

    @Test
    void getSubsistenceFeesRuns_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).userId("userId").build();
        final int page = 0;
        final int pageSize = 30;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
        	.when(roleAuthorizationService)
        	.evaluate(user, new String[] {REGULATOR});

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get(SUBSISTENCE_FEES_CONTROLLER_PATH)
        		.param("page", String.valueOf(pagingRequest.getPageNumber()))
                .param("size", String.valueOf(pagingRequest.getPageSize()))
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isForbidden());

        verify(subsistenceFeesRunQueryService, never()).getSubsistenceFeesRuns(user, pagingRequest);
    }
    
    @Test
    void getSubsistenceFeesRunDetailsById() throws Exception {
    	Long runId = 1L;
        LocalDateTime date = LocalDateTime.now();

        SubsistenceFeesRunDetailsDTO sfrDetailsDTO = new SubsistenceFeesRunDetailsDTO(1L, "S2501", date, 
        		PaymentStatus.AWAITING_PAYMENT, BigDecimal.valueOf(1000L), BigDecimal.valueOf(1000L), BigDecimal.valueOf(1000L), 1L, 1L);

        when(subsistenceFeesRunQueryService.getSubsistenceFeesRunDetailsById(runId)).thenReturn(sfrDetailsDTO);

        mockMvc.perform(MockMvcRequestBuilders
            .get(SUBSISTENCE_FEES_CONTROLLER_PATH + runId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paymentRequestId").value("S2501"));

        verify(subsistenceFeesRunQueryService, times(1)).getSubsistenceFeesRunDetailsById(runId);
    }

    @Test
    void getSubsistenceFeesRunDetailsById_forbidden() throws Exception {
    	Long runId = 1L;
        AppUser appUser = AppUser.builder()
            .userId("userId")
            .roleType(SECTOR_USER)
            .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(appUser, "getSubsistenceFeesRunDetailsById", Long.toString(runId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
            .get(SUBSISTENCE_FEES_CONTROLLER_PATH + runId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(subsistenceFeesRunQueryService);
    }
    
    @Test
    void getSubsistenceFeesRunMoas() throws Exception {
        final AppUser user = AppUser.builder().roleType(REGULATOR).build();
        final int page = 0;
        final int pageSize = 30;
        final long runId = 1L;
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

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(subsistenceFeesMoaQueryService.getSubsistenceFeesRunMoas(runId, criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.post(SUBSISTENCE_FEES_CONTROLLER_PATH + runId + "/moas")
        		.content(mapper.writeValueAsString(criteria))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.subsistenceFeesMoas[0].moaId").value(1L))
                .andExpect(jsonPath("$.subsistenceFeesMoas[0].transactionId").value("CCACM1200"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(subsistenceFeesMoaQueryService, times(1)).getSubsistenceFeesRunMoas(runId,criteria);
    }

    @Test
    void getSubsistenceFeesRunMoas_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).userId("userId").build();
        final int page = 0;
        final int pageSize = 30;
        final long runId = 1L;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
        		.paging(pagingRequest)
        		.moaType(MoaType.SECTOR_MOA)
        		.build();
        
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
        	.when(appUserAuthorizationService)
        	.authorize(user, "getSubsistenceFeesRunMoas", String.valueOf(1L), null, null);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(SUBSISTENCE_FEES_CONTROLLER_PATH + runId + "/moas")
        		.content(mapper.writeValueAsString(criteria))
                .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isForbidden());

        verify(subsistenceFeesMoaQueryService, never()).getSubsistenceFeesRunMoas(runId, criteria);
    }
}
