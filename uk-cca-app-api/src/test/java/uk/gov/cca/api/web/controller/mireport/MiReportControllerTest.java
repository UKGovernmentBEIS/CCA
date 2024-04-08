package uk.gov.cca.api.web.controller.mireport;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.AuthorityStatus;
import uk.gov.cca.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.cca.api.web.controller.mireport.MiReportController;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.cca.api.mireport.common.MiReportService;
import uk.gov.cca.api.mireport.common.MiReportType;
import uk.gov.cca.api.mireport.common.accountuserscontacts.AccountUserContact;
import uk.gov.cca.api.mireport.common.accountuserscontacts.AccountsUsersContactsMiReportResult;
import uk.gov.cca.api.mireport.common.domain.MiReportEntity;
import uk.gov.cca.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.cca.api.mireport.common.domain.dto.MiReportSearchResult;
import uk.gov.cca.api.mireport.common.outstandingrequesttasks.OutstandingRequestTasksReportService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.security.AppSecurityComponent;
import uk.gov.cca.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.cca.api.web.security.AuthorizedRoleAspect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType.DUMMY_REQUEST_TYPE_APPLICATION_REVIEW;
import static uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType.DUMMY_REQUEST_TASK_TYPE2;

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
    public void setUp() {
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
            .andExpect(jsonPath("$.[0].miReportType").value(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS.name()))
        ;

        verify(miReportService, times(1))
            .findByCompetentAuthority(appUser.getCompetentAuthority());
    }

    @Test
    void getCurrentUserReports_forbidden() throws Exception {
        AppUser appUser = AppUser.builder().roleType(RoleType.VERIFIER).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new RoleType[]{RoleType.REGULATOR});

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
        AccountUserContact accountUserContact = accountsUsersContactsMiReport.getResults().get(0);
        AppUser appUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(miReportService.generateReport(appUser.getCompetentAuthority(), reportParams))
            .thenReturn(miReportResult);

        mockMvc.perform(MockMvcRequestBuilders
                .post(MI_REPORT_BASE_CONTROLLER_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportParams)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reportType").value(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS.name()))
            .andExpect(jsonPath("$.results[0].Name").value(accountUserContact.getName()))
            .andExpect(jsonPath("$.results[0].Telephone").value(accountUserContact.getTelephone()))
            .andExpect(jsonPath("$.results[0].['Last logon']").value(accountUserContact.getLastLogon()))
            .andExpect(jsonPath("$.results[0].Email").value(accountUserContact.getEmail()))
            .andExpect(jsonPath("$.results[0].['User role']").value(accountUserContact.getRole()))
            .andExpect(jsonPath("$.results[0].['Account ID']").value(accountUserContact.getAccountId()))
            .andExpect(jsonPath("$.results[0].['Account name']").value(accountUserContact.getAccountName()))
            .andExpect(jsonPath("$.results[0].['Account status']").value(accountUserContact.getAccountStatus().toString()))
            .andExpect(jsonPath("$.results[0].['User status']").value(accountUserContact.getAuthorityStatus().toString()))
            .andExpect(jsonPath("$.results[0].['Is User Financial contact?']").value(accountUserContact.getFinancialContact()))
            .andExpect(jsonPath("$.results[0].['Is User Primary contact?']").value(accountUserContact.getPrimaryContact()))
            .andExpect(jsonPath("$.results[0].['Is User Secondary contact?']").value(accountUserContact.getSecondaryContact()))
            .andExpect(jsonPath("$.results[0].['Is User Service contact?']").value(accountUserContact.getServiceContact()))
            .andExpect(jsonPath("$.results[0].['Permit ID']").value(accountUserContact.getPermitId()));
        verify(miReportService, times(1))
            .generateReport(appUser.getCompetentAuthority(), reportParams);
    }

    @Test
    void getReport_not_found() throws Exception {
        AppUser appUser = buildMockAuthenticatedUser();
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
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
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(appUser, new RoleType[]{RoleType.REGULATOR});

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
            .thenReturn(Set.of(DUMMY_REQUEST_TYPE_APPLICATION_REVIEW, DUMMY_REQUEST_TASK_TYPE2));

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
            .evaluate(appUser, new RoleType[]{RoleType.REGULATOR});

        mockMvc.perform(MockMvcRequestBuilders
                .get(MI_REPORT_BASE_CONTROLLER_PATH + REQUEST_TASK_TYPES_CONTROLLER_PATH))
            .andExpect(status().isForbidden());

        verifyNoInteractions(outstandingRequestTasksReportService);
    }

    private MiReportResult buildMockMiAccountsUsersContactsReport() {
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
            .financialContact(Boolean.TRUE)
            .primaryContact(Boolean.TRUE)
            .secondaryContact(Boolean.FALSE)
            .serviceContact(Boolean.FALSE)
            .permitId("Permit id 1")
            .build();

        return AccountsUsersContactsMiReportResult.builder()
            .reportType(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS)
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
            .roleType(RoleType.REGULATOR)
            .userId(USER_ID)
            .build();
    }

    private List<MiReportSearchResult> buildMockMiReports() {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return Arrays.stream(MiReportType.values())
            .map(t -> MiReportEntity.builder().miReportType(t).competentAuthority(CompetentAuthorityEnum.ENGLAND))
            .map(e -> factory.createProjection(MiReportSearchResult.class, e))
            .collect(Collectors.toList());
    }

}