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

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.dto.FacilityPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.service.FacilityPerformanceAccountTemplateDataQueryService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

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
class FacilityPerformanceAccountTemplateDataReportControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/facilities/{facilityId}/performance-account-template-data-report";

    private MockMvc mockMvc;

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataReportController controller;

    @Mock
    private FacilityPerformanceAccountTemplateDataQueryService facilityPerformanceAccountTemplateDataQueryService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (FacilityPerformanceAccountTemplateDataReportController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addConverter(String.class, Year.class, Year::parse);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice()).setConversionService(conversionService).build();
    }

    @Test
    void getFacilityPerformanceAccountTemplateDataReportInfo() throws Exception {
        final Long facilityId = 912L;
        final Year targetPeriodYear = Year.of(2026);
        AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();

        final FacilityPerformanceAccountTemplateDataReportInfoDTO result = FacilityPerformanceAccountTemplateDataReportInfoDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(2)
                .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        when(facilityPerformanceAccountTemplateDataQueryService.getFacilityPerformanceAccountTemplateDataReportInfo(facilityId, targetPeriodYear))
                .thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/info")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.targetPeriodYear").value("2026"))
                .andExpect(jsonPath("$.reportVersion").value(2));

        verify(facilityPerformanceAccountTemplateDataQueryService, times(1))
                .getFacilityPerformanceAccountTemplateDataReportInfo(facilityId, targetPeriodYear);
    }

    @Test
    void getFacilityPerformanceAccountTemplateDataReportInfo_Forbidden() throws Exception {
        final Long facilityId = 912L;
        final Year targetPeriodYear = Year.of(2026);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(appSecurityComponent)
                .getAuthenticatedUser();

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/info")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andDo(print());

        verifyNoInteractions(facilityPerformanceAccountTemplateDataQueryService);
    }

    @Test
    void getFacilityPerformanceAccountTemplateDataReportDetails() throws Exception {
        final Long facilityId = 912L;
        final Year targetPeriodYear = Year.of(2026);
        AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();

        final FacilityPerformanceAccountTemplateDataReportDetailsDTO result = FacilityPerformanceAccountTemplateDataReportDetailsDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .submissionDate(LocalDate.of(2026, 1, 1).atStartOfDay())
                .data(FacilityPerformanceAccountTemplateDataContainer.builder()
                        .savingActions(List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
                                .actionCategoryType(ActionCategoryType.NO_ACTION)
                                .notes("Notes")
                                .build()))
                        .build())
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        when(facilityPerformanceAccountTemplateDataQueryService.getFacilityPerformanceAccountTemplateDataReportDetails(facilityId, targetPeriodYear))
                .thenReturn(result);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/details")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.targetPeriodYear").value("2026"))
                .andExpect(jsonPath("$.data.savingActions[0].actionCategoryType").value(ActionCategoryType.NO_ACTION.name()))
                .andExpect(jsonPath("$.data.savingActions[0].notes").value("Notes"));

        verify(facilityPerformanceAccountTemplateDataQueryService, times(1))
                .getFacilityPerformanceAccountTemplateDataReportDetails(facilityId, targetPeriodYear);
    }

    @Test
    void getFacilityPerformanceAccountTemplateDataReportDetails_Forbidden() throws Exception {
        final Long facilityId = 912L;
        final Year targetPeriodYear = Year.of(2026);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(appSecurityComponent)
                .getAuthenticatedUser();

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/details")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andDo(print());

        verifyNoInteractions(facilityPerformanceAccountTemplateDataQueryService);
    }
}
