package uk.gov.cca.api.web.controller.mireport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemParamsTypesProvider;
import uk.gov.netz.api.mireport.jsonprovider.MiReportSystemResultTypesProvider;
import uk.gov.netz.api.mireport.system.accountuserscontacts.AccountUserContact;
import uk.gov.netz.api.mireport.system.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.netz.api.mireport.system.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemEntity;
import uk.gov.netz.api.mireport.system.MiReportSystemSearchResult;
import uk.gov.netz.api.mireport.system.MiReportSystemService;
import uk.gov.netz.api.mireport.system.MiReportSystemType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MiReportSystemControllerTest {

    private static final String MI_REPORT_BASE_CONTROLLER_PATH = "/v1.0/mireports/system";
    private static final String REQUEST_TASK_TYPES_CONTROLLER_PATH = "/request-task-types";

    private MockMvc mockMvc;

    @InjectMocks
    private MiReportSystemController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private MiReportSystemService miReportSystemService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private OutstandingRequestTasksReportService outstandingRequestTasksReportService;

    private ObjectMapper objectMapper;

    private static final String USER_ID = "userId";
    private static final String ACCOUNT_ID = "emitterId";

    @BeforeEach
    void setUp() {
    	objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerSubtypes(new MiReportSystemParamsTypesProvider().getTypes().toArray(NamedType[]::new));
        objectMapper.registerSubtypes(new MiReportSystemResultTypesProvider().getTypes().toArray(NamedType[]::new));
        
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (MiReportSystemController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
            .setMessageConverters(mappingJackson2HttpMessageConverter)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setConversionService(conversionService)
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void getCurrentUserReports() throws Exception {
        List<MiReportSystemSearchResult> searchResults = buildMockMiReports();
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportSystemService.findByCompetentAuthority(appUser.getCompetentAuthority()))
            .thenReturn(searchResults);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH + "/types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(searchResults.size()))
            .andExpect(jsonPath("$.[0].miReportType").value(searchResults.getFirst().getMiReportType()))
        ;

        verify(miReportSystemService, times(1))
            .findByCompetentAuthority(appUser.getCompetentAuthority());
    }

    @Test
    void getCurrentUserReports_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleTypeConstants.VERIFIER).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH + "/types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verifyNoInteractions(miReportSystemService);
    }

    @Test
    void getReportBy_LIST_OF_ACCOUNTS_USERS_CONTACTS() throws Exception {
    	AccountsUsersContactsMiReportResult<AccountUserContact> miReportResult = buildMockMiAccountsUsersContactsReport();
        AccountUserContact accountUserContact = miReportResult.getResults().get(0);
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportSystemService.generateReport(appUser.getCompetentAuthority(), reportParams))
            .thenReturn(miReportResult);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportType").value(MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS))
            .andExpect(jsonPath("$.results[0].Name").value(accountUserContact.getName()))
            .andExpect(jsonPath("$.results[0].Telephone").value(accountUserContact.getTelephone()))
            .andExpect(jsonPath("$.results[0].['Last logon']").value(accountUserContact.getLastLogon()))
            .andExpect(jsonPath("$.results[0].Email").value(accountUserContact.getEmail()))
            .andExpect(jsonPath("$.results[0].['User role']").value(accountUserContact.getRole()))
            .andExpect(jsonPath("$.results[0].['Account ID']").value(accountUserContact.getAccountId()))
            .andExpect(jsonPath("$.results[0].['Account name']").value(accountUserContact.getAccountName()))
            .andExpect(jsonPath("$.results[0].['Account status']").value(accountUserContact.getAccountStatus()))
            .andExpect(jsonPath("$.results[0].['User status']").value(accountUserContact.getAuthorityStatus()))
            .andExpect(jsonPath("$.results[0].['Is User Primary contact?']").value(accountUserContact.getPrimaryContact()));
        verify(miReportSystemService, times(1))
            .generateReport(appUser.getCompetentAuthority(), reportParams);
    }

    @Test
    void getReport_not_found() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED))
            .when(miReportSystemService).generateReport(appUser.getCompetentAuthority(), reportParams);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isConflict());

        verify(miReportSystemService, times(1))
            .generateReport(appUser.getCompetentAuthority(), reportParams);
    }

    @Test
    void getReport_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(miReportSystemService);
    }

    @Test
    void retrieveRegulatorRequestTaskTypes() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(outstandingRequestTasksReportService.getRequestTaskTypesByRoleType(appUser.getRoleType()))
            .thenReturn(Set.of("DUMMY_REQUEST_TYPE_APPLICATION_REVIEW", "DUMMY_REQUEST_TASK_TYPE2"));

        mockMvc.perform(MockMvcRequestBuilders
                .get(MI_REPORT_BASE_CONTROLLER_PATH + REQUEST_TASK_TYPES_CONTROLLER_PATH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(Matchers.containsInAnyOrder("DUMMY_REQUEST_TYPE_APPLICATION_REVIEW", "DUMMY_REQUEST_TASK_TYPE2")));

        verify(outstandingRequestTasksReportService, times(1))
            .getRequestTaskTypesByRoleType(appUser.getRoleType());
    }

    @Test
    void retrieveRegulatorRequestTaskTypes_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .get(MI_REPORT_BASE_CONTROLLER_PATH + REQUEST_TASK_TYPES_CONTROLLER_PATH))
            .andExpect(status().isForbidden());

        verifyNoInteractions(outstandingRequestTasksReportService);
    }

    private AccountsUsersContactsMiReportResult<AccountUserContact> buildMockMiAccountsUsersContactsReport() {
    	String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        AccountUserContact accountUserContact = AccountUserContact.builder()
            .name("Foo Bar")
            .telephone("")
            .lastLogon("")
            .email("test@test.com")
            .role("Operator")
            .email(ACCOUNT_ID)
            .accountName("account name")
            .accountStatus("accountStatus")
            .authorityStatus(AuthorityStatus.ACTIVE.name())
            .primaryContact(Boolean.TRUE)
            .build();

        return AccountsUsersContactsMiReportResult.builder()
            .reportType(reportType)
            .results(List.of(accountUserContact))
            .build();
    }

    private AppUser buildMockAuthenticatedUser() {
        return AppUser.builder()
            .authorities(
                Arrays.asList(
                    AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()
                )
            )
            .roleType(RoleTypeConstants.REGULATOR)
            .userId(USER_ID)
            .build();
    }

    private List<MiReportSystemSearchResult> buildMockMiReports() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return Set.of(MiReportSystemType.LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS,
                MiReportSystemType.REGULATOR_OUTSTANDING_REQUEST_TASKS,
                MiReportSystemType.COMPLETED_WORK,
                MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS)
        		.stream()
        		.map(type -> MiReportSystemEntity.builder().miReportType(type).competentAuthority(CompetentAuthorityEnum.ENGLAND))
        		.map(e -> factory.createProjection(MiReportSystemSearchResult.class, e))
        		.toList();
    }

}