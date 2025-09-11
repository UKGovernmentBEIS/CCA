package uk.gov.cca.api.web.controller.targetperiod;

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
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class CertificationPeriodViewControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/certification-period/";

    private MockMvc mockMvc;

    @InjectMocks
    private CertificationPeriodViewController certificationPeriodViewController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private CertificationPeriodService certificationPeriodService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(certificationPeriodViewController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        certificationPeriodViewController = (CertificationPeriodViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(certificationPeriodViewController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getCurrentCertificationPeriod() throws Exception {
        final AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();

        final CertificationPeriodInfoDTO certificationPeriodInfoDTO = CertificationPeriodInfoDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2023, 7, 1))
                .endDate(LocalDate.of(2025, 6, 30))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(submitter);
        when(certificationPeriodService.getCurrentCertificationPeriod()).thenReturn(certificationPeriodInfoDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + "/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(certificationPeriodInfoDTO.getId()));

        verify(certificationPeriodService, times(1)).getCurrentCertificationPeriod();
    }
}
