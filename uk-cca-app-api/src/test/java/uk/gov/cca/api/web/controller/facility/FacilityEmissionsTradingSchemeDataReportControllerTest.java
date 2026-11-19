package uk.gov.cca.api.web.controller.facility;

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
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service.EmissionsTradingSchemeDataQueryService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.time.LocalDateTime;
import java.time.Year;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class FacilityEmissionsTradingSchemeDataReportControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/facilities/{facilityId}/emissions-trading-scheme-data-report";

    private MockMvc mockMvc;

    @InjectMocks
    private FacilityEmissionsTradingSchemeDataReportController controller;

    @Mock
    private EmissionsTradingSchemeDataQueryService emissionsTradingSchemeDataQueryService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
                new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (FacilityEmissionsTradingSchemeDataReportController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        conversionService.addConverter(String.class, Year.class, Year::parse);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService).build();
    }

    @Test
    void getEmissionsTradingSchemeDataReportInfo() throws Exception {
        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 3;
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);
        final Long facilityId = 42L;
        final AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();

        final EmissionsTradingSchemeDataReportInfoDTO emissionsTradingSchemeDataReportInfoDTO = EmissionsTradingSchemeDataReportInfoDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(reportVersion)
                .submissionDate(submissionDate)
                .build();

        when(emissionsTradingSchemeDataQueryService.getEmissionsTradingSchemeDataReportInfo(facilityId, targetPeriodYear))
                .thenReturn(emissionsTradingSchemeDataReportInfoDTO);
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/info")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.targetPeriodYear").value(targetPeriodYear.toString()))
                .andExpect(jsonPath("$.reportVersion").value(reportVersion));

        verify(emissionsTradingSchemeDataQueryService, times(1))
                .getEmissionsTradingSchemeDataReportInfo(facilityId, targetPeriodYear);
    }

    @Test
    void getEmissionsTradingSchemeDataReportInfo_Forbidden() throws Exception {
        final Long facilityId = 912L;
        final Year targetPeriodYear = Year.of(2026);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(appSecurityComponent)
                .getAuthenticatedUser();

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/info")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andDo(print());

        verifyNoInteractions(emissionsTradingSchemeDataQueryService);
    }
}
