package uk.gov.cca.api.web.controller.performancedata;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportTypeDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class PerformanceDataReportingViewControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/target-period-reporting/performance-data/";

    private MockMvc mockMvc;

    @InjectMocks
    private PerformanceDataReportingViewController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private PerformanceDataService performanceDataReportService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (PerformanceDataReportingViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getAvailableTargetPeriodsForPerformanceDataReporting() throws Exception {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final AppUser user = AppUser.builder().roleType(REGULATOR).build();

        final PerformanceDataReportTypeDTO targetPeriodReport = PerformanceDataReportTypeDTO.builder()
                .targetPeriodType(TargetPeriodType.TP8)
                .reportType(PerformanceDataReportType.INTERIM)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(performanceDataReportService.getAvailableTargetPeriodsForPerformanceDataReporting(schemeVersion))
                .thenReturn(List.of(targetPeriodReport));

        mockMvc.perform(get(CONTROLLER_PATH + "/available-target-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("scheme",schemeVersion.name()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$[0].targetPeriodType").value(TargetPeriodType.TP8.name()))
                .andExpect(jsonPath("$[0].reportType").value(PerformanceDataReportType.INTERIM.name()));

        verify(performanceDataReportService, times(1))
                .getAvailableTargetPeriodsForPerformanceDataReporting(schemeVersion);
    }
}
