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
import uk.gov.netz.api.mireport.MiReportService;
import uk.gov.netz.api.mireport.MiReportType;
import uk.gov.netz.api.mireport.accountuserscontacts.AccountUserContact;
import uk.gov.netz.api.mireport.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;
import uk.gov.netz.api.mireport.domain.MiReportEntity;
import uk.gov.netz.api.mireport.domain.MiReportResult;
import uk.gov.netz.api.mireport.domain.MiReportSearchResult;
import uk.gov.netz.api.mireport.jsonprovider.MiReportParamsTypesProvider;
import uk.gov.netz.api.mireport.jsonprovider.MiReportResultTypesProvider;
import uk.gov.netz.api.mireport.outstandingrequesttasks.OutstandingRequestTasksReportService;

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
class MiReportControllerTest {

    private static final String MI_REPORT_BASE_CONTROLLER_PATH = "/v1.0/mireports";
    private static final String REQUEST_TASK_TYPES_CONTROLLER_PATH = "/request-task-types";

    private MockMvc mockMvc;

    @InjectMocks
    private MiReportController miReportController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private MiReportService miReportService;

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
        objectMapper.registerSubtypes(new MiReportParamsTypesProvider().getTypes().toArray(NamedType[]::new));
        objectMapper.registerSubtypes(new MiReportResultTypesProvider().getTypes().toArray(NamedType[]::new));
        
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedRoleAspect
            authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(miReportController);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);
        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        miReportController = (MiReportController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(miReportController)
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
        List<MiReportSearchResult> searchResults = buildMockMiReports();
        AppUser appUser = buildMockAuthenticatedUser();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportService.findByCompetentAuthority(appUser.getCompetentAuthority()))
            .thenReturn(searchResults);

        mockMvc.perform(MockMvcRequestBuilders.get(MI_REPORT_BASE_CONTROLLER_PATH + "/types")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(searchResults.size()))
            .andExpect(jsonPath("$.[0].miReportType").value(searchResults.getFirst().getMiReportType()))
        ;

        verify(miReportService, times(1))
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

        verifyNoInteractions(miReportService);
    }

    @Test
    void getReportBy_LIST_OF_ACCOUNTS_USERS_CONTACTS() throws Exception {
        MiReportResult miReportResult = buildMockMiAccountsUsersContactsReport();
        AccountsUsersContactsMiReportResult
            accountsUsersContactsMiReport = (AccountsUsersContactsMiReportResult) miReportResult;
        AccountUserContact accountUserContact = (AccountUserContact) accountsUsersContactsMiReport.getResults().get(0);
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportService.generateReport(appUser.getCompetentAuthority(), reportParams))
            .thenReturn(miReportResult);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportType").value(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS))
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
        verify(miReportService, times(1))
            .generateReport(appUser.getCompetentAuthority(), reportParams);
    }

    @Test
    void getReport_not_found() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED))
            .when(miReportService).generateReport(appUser.getCompetentAuthority(), reportParams);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isConflict());

        verify(miReportService, times(1))
            .generateReport(appUser.getCompetentAuthority(), reportParams);
    }

    @Test
    void getReport_forbidden() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new String[]{RoleTypeConstants.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(miReportService);
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

    private MiReportResult buildMockMiAccountsUsersContactsReport() {
    	String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
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

    private List<MiReportSearchResult> buildMockMiReports() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return Set.of(MiReportType.LIST_OF_ACCOUNTS_ASSIGNED_REGULATOR_SITE_CONTACTS,
                MiReportType.REGULATOR_OUTSTANDING_REQUEST_TASKS,
                MiReportType.COMPLETED_WORK,
                MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS,
                MiReportType.CUSTOM)
        		.stream()
        		.map(type -> MiReportEntity.builder().miReportType(type).competentAuthority(CompetentAuthorityEnum.ENGLAND))
        		.map(e -> factory.createProjection(MiReportSearchResult.class, e))
        		.toList();
    }

}