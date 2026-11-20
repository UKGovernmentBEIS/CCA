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
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFuel;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportDetailsDTO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import static uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataFacilityFixedConversionFactor.NATURAL_GAS;
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

    @Test
    void getEmissionsTradingSchemeDataReportDetails() throws Exception {

        final Year targetPeriodYear = Year.of(2026);
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);
        final Long facilityId = 42L;
        final AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();
        final MeasurementType measurementType = MeasurementType.CARBON_KG;
        final EmissionsTradingSchemeDataContainer dataContainer = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(measurementType)
                .uketsId("UK0012345")
                .isUltraSmallEmitter(false)
                .fuels(List.of(PerformanceDataFacilityFuel.builder()
                        .name(NATURAL_GAS.getDescription())
                        .fixedConversionFactorCode(NATURAL_GAS)
                        .conversionFactor(PerformanceDataFacilityFixedConversionFactor.getValueByMeasurementType(NATURAL_GAS, measurementType))
                        .deliveredEnergy(BigDecimal.valueOf(5000000))
                        .primaryConversionFactor(NATURAL_GAS.getPrimaryFactor())
                        .primaryEnergy(BigDecimal.valueOf(5000000))
                        .build()))
                .calculatedResults(EmissionsTradingSchemeDataCalculatedResults.builder()
                        .totalPrimaryEnergy(BigDecimal.valueOf(5000000))
                        .totalCo2Emissions(BigDecimal.valueOf(912.700))
                        .build())
                .build();

        final EmissionsTradingSchemeDataReportDetailsDTO emissionsTradingSchemeDataReportDetailsDTO = EmissionsTradingSchemeDataReportDetailsDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .submissionDate(submissionDate)
                .data(dataContainer)
                .build();

        when(emissionsTradingSchemeDataQueryService.getEmissionsTradingSchemeDataReportDetails(facilityId, targetPeriodYear))
                .thenReturn(emissionsTradingSchemeDataReportDetailsDTO);
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/details")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.isUltraSmallEmitter").value(dataContainer.getIsUltraSmallEmitter()))
                .andExpect(jsonPath("$.data.fuels[0].name").value(dataContainer.getFuels().getFirst().getName()))
                .andExpect(jsonPath("$.data.calculatedResults.totalPrimaryEnergy").value(dataContainer.getCalculatedResults().getTotalPrimaryEnergy()));

        verify(emissionsTradingSchemeDataQueryService, times(1))
                .getEmissionsTradingSchemeDataReportDetails(facilityId, targetPeriodYear);
    }

    @Test
    void getEmissionsTradingSchemeDataReportDetails_not_found() throws Exception {

        final Year targetPeriodYear = Year.of(2026);
        final Long facilityId = 42L;
        final AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
                .when(emissionsTradingSchemeDataQueryService)
                .getEmissionsTradingSchemeDataReportDetails(facilityId, targetPeriodYear);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/details")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(emissionsTradingSchemeDataQueryService, times(1))
                .getEmissionsTradingSchemeDataReportDetails(facilityId, targetPeriodYear);
    }

    @Test
    void getEmissionsTradingSchemeDataReportDetails_forbidden() throws Exception {

        final Year targetPeriodYear = Year.of(2026);
        final Long facilityId = 42L;

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appSecurityComponent).getAuthenticatedUser();

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/details")
                        .param("targetPeriodYear", targetPeriodYear.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(emissionsTradingSchemeDataQueryService);
    }
}
