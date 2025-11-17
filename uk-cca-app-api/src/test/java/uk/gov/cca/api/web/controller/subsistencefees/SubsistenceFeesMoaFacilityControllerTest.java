package uk.gov.cca.api.web.controller.subsistencefees;

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
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFacilityMarkingStatusService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaFacilityControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/subsistence-fees/moa-facilities/";

    private MockMvc mockMvc;

    @InjectMocks
    private SubsistenceFeesMoaFacilityController subsistenceFeesMoaFacilityController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private SubsistenceFeesMoaFacilityMarkingStatusService subsistenceFeesMoaFacilityMarkingStatusService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(subsistenceFeesMoaFacilityController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        subsistenceFeesMoaFacilityController = (SubsistenceFeesMoaFacilityController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(subsistenceFeesMoaFacilityController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getSubsistenceFeesMoaFacilityMarkingStatusHistoryInfo() throws Exception {
        LocalDateTime submissionDate = LocalDateTime.now();
        Long moaFacilityId = 1L;

        SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO subsistenceFeesMoaFacilityMarkingStatusHistoryDTO = SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO.builder()
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .submitter("submitter")
                .submissionDate(submissionDate)
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO subsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO = SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO.builder()
                .facilityBusinessId("Facility Id")
                .siteName("Facility Name")
                .markingStatusHistoryList(List.of(subsistenceFeesMoaFacilityMarkingStatusHistoryDTO))
                .build();

        when(subsistenceFeesMoaFacilityMarkingStatusService.getMoaFacilityMarkingStatusHistoryInfo(moaFacilityId))
                .thenReturn(subsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + moaFacilityId + "/marking-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.siteName").value(subsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO.getSiteName()));

        verify(subsistenceFeesMoaFacilityMarkingStatusService, times(1)).getMoaFacilityMarkingStatusHistoryInfo(moaFacilityId);
    }
}
