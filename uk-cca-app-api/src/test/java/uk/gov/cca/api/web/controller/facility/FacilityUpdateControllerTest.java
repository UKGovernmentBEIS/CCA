package uk.gov.cca.api.web.controller.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.cca.api.facility.domain.dto.UpdateFacilitySchemeExitDateDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.facility.service.FacilityInfoServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class FacilityUpdateControllerTest {

    private static final String BASE_PATH = "/v1.0/facilities/1";

    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @InjectMocks
    private FacilityUpdateController controller;

    @Mock
    private FacilityDataUpdateService facilityDataUpdateService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private FacilityInfoServiceOrchestrator facilityInfoServiceOrchestrator;


    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (FacilityUpdateController) aopProxy.getProxy();

        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .setValidator(Mockito.mock(Validator.class))
                .build();
    }

    @Test
    void updateFacilitySchemeExitDate() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final UpdateFacilitySchemeExitDateDTO facilitySchemeExitDateDTO = UpdateFacilitySchemeExitDateDTO.builder()
                .schemeExitDate(LocalDate.of(2023, 2, 12))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/scheme-exit-date")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(facilitySchemeExitDateDTO)))
                .andExpect(status().isNoContent());


        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(facilityDataUpdateService, times(1))
                .updateFacilitySchemeExitDate(1L, facilitySchemeExitDateDTO.getSchemeExitDate());
    }

    @Test
    void updateFacilitySchemeExitDate_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();
        final UpdateFacilitySchemeExitDateDTO facilitySchemeExitDateDTO = UpdateFacilitySchemeExitDateDTO.builder()
                .schemeExitDate(LocalDate.of(2023, 2, 12))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateFacilitySchemeExitDate", "1", null, null);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "/scheme-exit-date")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(facilitySchemeExitDateDTO)))
                .andExpect(status().isForbidden());


        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(facilityDataUpdateService);
    }

    @Test
    void updateFacilityCertificationStatus() {

        final FacilityCertificationStatusUpdateDTO facilityCertificationStatusUpdateDTO = FacilityCertificationStatusUpdateDTO.builder()
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriodId(2L)
                .build();
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        ResponseEntity<Void> result = controller.updateFacilityCertificationStatus(1L, facilityCertificationStatusUpdateDTO);

        assertEquals(new ResponseEntity<Void>(HttpStatus.NO_CONTENT), result);
        verify(facilityInfoServiceOrchestrator, times(1))
                .updateFacilityCertificationStatus(1L, facilityCertificationStatusUpdateDTO);
    }

}
