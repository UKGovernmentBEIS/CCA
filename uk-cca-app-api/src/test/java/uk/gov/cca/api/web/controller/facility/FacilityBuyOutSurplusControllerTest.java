package uk.gov.cca.api.web.controller.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class FacilityBuyOutSurplusControllerTest {

    private static final String REQUEST_PATH = "/v1.0/facilities/{facilityId}/buy-out-surplus/";

    private ObjectMapper mapper;

    private MockMvc mockMvc;
    @InjectMocks
    private FacilityBuyOutSurplusController controller;

    @Mock
    private SurplusService surplusService;

    @Mock
    private SurplusQueryService surplusQueryService;

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
        controller = (FacilityBuyOutSurplusController) aopProxy.getProxy();

        mapper = new ObjectMapper();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .setValidator(Mockito.mock(Validator.class))
                .build();
    }

    @Test
    void updateFacilitySurplusGained() throws Exception {

        final long facilityId = 999L;
        final String POST_PATH = REQUEST_PATH.replace("{facilityId}", String.valueOf(facilityId));
        final SurplusUpdateDTO surplusUpdateDTO = SurplusUpdateDTO.builder()
                .newSurplusGained(BigDecimal.TEN)
                .comments("comments")
                .build();
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .roleType(RoleTypeConstants.REGULATOR)
                .build();
        Mockito.when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);

        mockMvc.perform(post(POST_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(surplusUpdateDTO)))
                .andExpect(status().isNoContent());

        verify(surplusService, times(1))
                .updateFacilitySurplusGained(surplusUpdateDTO, facilityId, appUser);
    }

    @Test
    void getFacilitySurplusHistory() throws Exception {
        final long facilityId = 999L;
        final String GET_PATH = REQUEST_PATH.replace("{facilityId}", String.valueOf(facilityId)) + "history";

        final List<SurplusHistoryDTO> surplusHistoryDTOS = List.of(
                SurplusHistoryDTO.builder()
                        .surplusGained(BigDecimal.TEN)
                        .submitter("Regulator")
                        .comments("comments")
                        .build()
        );
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(surplusQueryService.getFacilitySurplusHistory(facilityId))
                .thenReturn(surplusHistoryDTOS);

        mockMvc.perform(get(GET_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$[0].submitter").value(surplusHistoryDTOS.get(0).getSubmitter()))
                .andExpect(jsonPath("$[0].comments").value(surplusHistoryDTOS.get(0).getComments()))
                .andExpect(jsonPath("$[0].surplusGained").value(surplusHistoryDTOS.get(0).getSurplusGained()));

        verify(surplusQueryService, times(1))
                .getFacilitySurplusHistory(facilityId);
    }
}
