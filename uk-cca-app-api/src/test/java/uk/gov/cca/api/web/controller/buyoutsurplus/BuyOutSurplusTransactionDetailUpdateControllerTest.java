package uk.gov.cca.api.web.controller.buyoutsurplus;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailUpdateAmountDTO;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.buyoutsurplus.service.BuyOutSurplusTransactionUpdateServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class BuyOutSurplusTransactionDetailUpdateControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private static final String CONTROLLER_PATH = "/v1.0/buy-out-surplus/transaction-details/{id}/update";

    @InjectMocks
    private BuyOutSurplusTransactionDetailUpdateController controller;

    @Mock
    private BuyOutSurplusTransactionUpdateServiceOrchestrator buyOutSurplusTransactionUpdateServiceOrchestrator;

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
        controller = (BuyOutSurplusTransactionDetailUpdateController) aopProxy.getProxy();

        objectMapper = new ObjectMapper();

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .setMessageConverters(mappingJackson2HttpMessageConverter)
                .build();
    }

    @Test
    void updateBuyOutSurplusTransactionDetailAmount() throws Exception {
        final Long transactionDetailId = 99L;
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .roleType(RoleTypeConstants.REGULATOR)
                .build();
        final BuyOutSurplusTransactionDetailUpdateAmountDTO buyOutFeeUpdateDTO = BuyOutSurplusTransactionDetailUpdateAmountDTO.builder()
                .amount(BigDecimal.valueOf(10.53))
                .comments("comments")
                .build();

        when(appSecurityComponent.getAuthenticatedUser())
                .thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + "/amount", transactionDetailId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyOutFeeUpdateDTO)))
                .andExpect(status().isNoContent());

        verify(buyOutSurplusTransactionUpdateServiceOrchestrator, times(1))
                .updateTransactionDetailAmount(transactionDetailId, buyOutFeeUpdateDTO, appUser);

    }
}
