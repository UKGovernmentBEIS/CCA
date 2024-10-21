package uk.gov.cca.api.web.controller.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoResponseDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountHeaderInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.account.dto.TargetUnitAccountDetailsResponseDTO;
import uk.gov.cca.api.web.orchestrator.account.service.TargetUnitAccountQueryServiceOrchestrator;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchResultInfoDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.service.AccountSearchServiceDelegator;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class TargetUnitAccountViewControllerTest {

    private static final String BASE_PATH = "/v1.0/target-unit-accounts/";

    private MockMvc mockMvc;

    @InjectMocks
    private TargetUnitAccountViewController controller;

    @Mock
    private TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @Mock
    private TargetUnitAccountQueryServiceOrchestrator targetUnitAccountQueryServiceOrchestrator;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AccountSearchServiceDelegator accountSearchServiceDelegator;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (TargetUnitAccountViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setConversionService(conversionService)
            .build();
    }

    @Test
    void getTargetUnitAccountDetailsById() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final TargetUnitAccountDetailsResponseDTO targetUnitAccountDetailsResponse = TargetUnitAccountDetailsResponseDTO.builder()
                .subsectorAssociation(SubsectorAssociationDTO.builder()
                        .name("Name")
                        .build())
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(targetUnitAccountQueryServiceOrchestrator.getTargetUnitAccountDetailsById(accountId))
                .thenReturn(targetUnitAccountDetailsResponse);

        mockMvc.perform(get(BASE_PATH + accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.subsectorAssociation.name").value("Name"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountQueryServiceOrchestrator, times(1))
                .getTargetUnitAccountDetailsById(accountId);
    }

    @Test
    void getTargetUnitAccountDetailsById_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getTargetUnitAccountDetailsById", Long.toString(accountId));

        mockMvc.perform(get(BASE_PATH + accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(targetUnitAccountQueryServiceOrchestrator);
    }

    @Test
    void getTargetUnitAccountsWithSiteContacts() throws Exception {
        final Long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        
        List<TargetUnitAccountInfoDTO> contacts = List.of(
            new TargetUnitAccountInfoDTO(1L, "ACC-T00001", "Acount name 1", TargetUnitAccountStatus.LIVE, "userId1"),
            new TargetUnitAccountInfoDTO(2L, "ACC-T00002", "Acount name 2",TargetUnitAccountStatus.LIVE, "userId2"));        

        TargetUnitAccountInfoResponseDTO targetUnitAccountInfoResponseDTO = TargetUnitAccountInfoResponseDTO.builder().accountsWithSiteContact(contacts).editable(true).totalItems(1L).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(targetUnitAccountSiteContactService.getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, 0, 2))
            .thenReturn(targetUnitAccountInfoResponseDTO);

        mockMvc.perform(get(BASE_PATH + "/sector-association/" + sectorAssociationId + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(jsonPath("$.accountsWithSiteContact[0].accountId").value(1L))
            .andExpect(jsonPath("$.accountsWithSiteContact[0].siteContactUserId").value("userId1"))
            .andExpect(jsonPath("$.accountsWithSiteContact[1].accountId").value(2L))
            .andExpect(jsonPath("$.accountsWithSiteContact[1].siteContactUserId").value("userId2"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountSiteContactService, times(1))
            .getTargetUnitAccountsWithSiteContact(user, sectorAssociationId, 0, 2);
    }

    @Test
    void getTargetUnitAccounts_forbidden() throws Exception {
        final long sectorAssociationId = 1L;
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
	        .when(appUserAuthorizationService)
	        .authorize(user, "getTargetUnitAccountsWithSiteContacts", Long.toString(sectorAssociationId));

        mockMvc.perform(get(BASE_PATH + "/sector-association/" + sectorAssociationId + "?page=0&size=2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountSiteContactService, never()).getTargetUnitAccountsWithSiteContact(any(), any(), any(), any());
    }


    @Test
    void searchUserAccountsTest() throws Exception {
        AppUser authUser = AppUser.builder()
                .userId("userId")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        final AccountSearchCriteria searchCriteria = getSearchCriteria();


        final AccountSearchResultInfoDTO accountSearchResultInfoDTO1 =
                new AccountSearchResultInfoDTO(1L, "Account_1", "business_id_1", TargetUnitAccountStatus.NEW);

        final AccountSearchResultInfoDTO accountSearchResultInfoDTO2 =
                new AccountSearchResultInfoDTO(2L, "Account_2", "business_id_2", TargetUnitAccountStatus.NEW);


        final AccountSearchResults accountSearchResults = AccountSearchResults.builder()
                .accounts(List.of(accountSearchResultInfoDTO1, accountSearchResultInfoDTO2))
                .total(2L)
                .build();

        //mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        when(accountSearchServiceDelegator.getAccountsByUserAndSearchCriteria(authUser, searchCriteria))
                .thenReturn(accountSearchResults);

        //invoke
        mockMvc.perform(MockMvcRequestBuilders
                        .get(BASE_PATH)
                        .param("term", searchCriteria.getTerm())
                        .param("page", String.valueOf(searchCriteria.getPaging().getPageNumber()))
                        .param("size", String.valueOf(searchCriteria.getPaging().getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.accounts[0].name").value(accountSearchResults.getAccounts().get(0).getName()))
                .andExpect(jsonPath("$.accounts[1].name").value(accountSearchResults.getAccounts().get(1).getName()));

        // verify
        verify(accountSearchServiceDelegator, times(1)).getAccountsByUserAndSearchCriteria(authUser, searchCriteria);
    }

    @Test
    void getAccountHeaderInfoById() throws Exception {
        Long accountId = 1L;
        String accountName = "Test Account";
        String businessId = "AIC/800544";
        TargetUnitAccountStatus status = TargetUnitAccountStatus.LIVE;
        AppUser authUser = AppUser.builder()
                .userId("userId")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
                .build();

        TargetUnitAccountHeaderInfoDTO targetUnitAccountHeaderInfoDTO = TargetUnitAccountHeaderInfoDTO.builder()
                .name(accountName)
                .businessId(businessId)
                .status(status).build();


        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        when(targetUnitAccountQueryService.getTargetUnitAccountHeaderInfo(accountId)).thenReturn(targetUnitAccountHeaderInfoDTO);

        mockMvc.perform(get(BASE_PATH + accountId + "/header-info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.name").value(accountName))
                .andExpect(jsonPath("$.businessId").value(businessId))
                .andExpect(jsonPath("$.status").value(status.getName()));

        // verify
        verify(targetUnitAccountQueryService, times(1)).getTargetUnitAccountHeaderInfo(accountId);
    }


    private AccountSearchCriteria getSearchCriteria() {
        String term = "NEW";
        final PagingRequest pageRequest = PagingRequest.builder()
                .pageSize(5L)
                .pageNumber(0L)
                .build();
        return AccountSearchCriteria.builder()
                .term(term)
                .paging(pageRequest)
                .build();
    }
}
