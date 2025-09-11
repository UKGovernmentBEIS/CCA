package uk.gov.cca.api.web.controller.sectorassociation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoResponseDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.sectorassociation.service.SectorAssociationTargetUnitAccountsServiceOrchestrator;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria.SortBy;
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
@EnableWebMvc
class SectorAssociationTargetUnitAccountsControllerTest {

	private static final String BASE_PATH = "/v1.0/sector-association/";

    private MockMvc mockMvc;

    @InjectMocks
    private SectorAssociationTargetUnitAccountsController controller;

    @Mock
    private SectorAssociationTargetUnitAccountsServiceOrchestrator sectorAssociationTargetUnitsServiceOrchestrator;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;
    
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (SectorAssociationTargetUnitAccountsController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();
        
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void getTargetUnitAccountsWithSiteContacts() throws Exception {
        final Long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        
        List<TargetUnitAccountInfoDTO> contacts = List.of(
            new TargetUnitAccountInfoDTO(1L, "ACC-T00001", "Acount name 1", TargetUnitAccountStatus.LIVE, "userId1"),
            new TargetUnitAccountInfoDTO(2L, "ACC-T00002", "Acount name 2",TargetUnitAccountStatus.LIVE, "userId2"));
        
		AccountSearchCriteria accountSearchCriteria = AccountSearchCriteria.builder()
				.paging(PagingRequest.builder().pageNumber(0).pageSize(2).build())
				.sortBy(SortBy.ACCOUNT_BUSINESS_ID).direction(Direction.ASC).build();

        TargetUnitAccountInfoResponseDTO targetUnitAccountInfoResponseDTO = TargetUnitAccountInfoResponseDTO.builder().accountsWithSiteContact(contacts).editable(true).totalItems(1L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(sectorAssociationTargetUnitsServiceOrchestrator.getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, accountSearchCriteria))
            .thenReturn(targetUnitAccountInfoResponseDTO);

        mockMvc.perform(get(BASE_PATH + sectorAssociationId + "/target-unit-accounts/" + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.accountsWithSiteContact[0].accountId").value(1L))
            .andExpect(jsonPath("$.accountsWithSiteContact[0].siteContactUserId").value("userId1"))
            .andExpect(jsonPath("$.accountsWithSiteContact[1].accountId").value(2L))
            .andExpect(jsonPath("$.accountsWithSiteContact[1].siteContactUserId").value("userId2"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationTargetUnitsServiceOrchestrator, times(1))
            .getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, accountSearchCriteria);
    }

    @Test
    void getTargetUnitAccounts_forbidden() throws Exception {
        final long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
	        .when(appUserAuthorizationService)
	        .authorize(user, "getTargetUnitAccountsWithSiteContacts", Long.toString(sectorAssociationId), null, null);

        mockMvc.perform(get(BASE_PATH + sectorAssociationId + "/target-unit-accounts/" + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationTargetUnitsServiceOrchestrator, never()).getTargetUnitAccountsWithSiteContact(any(), any(), any());
    }
    
    @Test
    void updateTargetUnitAccountSiteContacts() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final Long sectorAssociationId = 1L;
        List<TargetUnitAccountSiteContactDTO> siteContacts = List.of(
            TargetUnitAccountSiteContactDTO.builder().accountId(1L).userId("userId1").build(),
            TargetUnitAccountSiteContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(post(BASE_PATH + sectorAssociationId + "/target-unit-accounts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(siteContacts)))
            .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationTargetUnitsServiceOrchestrator, times(1)).updateTargetUnitAccountSiteContacts(user, sectorAssociationId, siteContacts);
    }

    @Test
    void updateTargetUnitAccountSiteContacts_forbidden() throws Exception {
        final Long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        List<TargetUnitAccountSiteContactDTO> siteContacts = List.of(
            TargetUnitAccountSiteContactDTO.builder().accountId(1L).userId("userId1").build(),
            TargetUnitAccountSiteContactDTO.builder().accountId(2L).userId("userId2").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(appUserAuthorizationService)
            .authorize(user, "updateTargetUnitAccountSiteContacts", Long.toString(sectorAssociationId), null, null);

        mockMvc.perform(post(BASE_PATH + sectorAssociationId + "/target-unit-accounts/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(siteContacts)))
            .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(sectorAssociationTargetUnitsServiceOrchestrator, never()).updateTargetUnitAccountSiteContacts(any(), any(), anyList());
    }
}
