package uk.gov.cca.api.web.controller.buyoutsurplus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionMarkPaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateReceivedAmountDTO;
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
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class BuyOutSurplusTransactionUpdateControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private static final String CONTROLLER_PATH = "/v1.0/buy-out-surplus/transactions/{id}/update";

    @InjectMocks
    private BuyOutSurplusTransactionUpdateController controller;

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
        controller = (BuyOutSurplusTransactionUpdateController) aopProxy.getProxy();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
    void updateBuyOutSurplusTransactionDetailsPaymentStatus() throws Exception {
        final Long transactionId = 99L;
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .roleType(RoleTypeConstants.REGULATOR)
                .build();
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.PAID)
                .comments("comments")
                .paymentDate(LocalDate.of(2025, 5, 15))
                .detailIds(Set.of(1L))
                .build();

        when(appSecurityComponent.getAuthenticatedUser())
                .thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + "/payment-status", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDTO)))
                .andExpect(status().isNoContent());

        verify(buyOutSurplusTransactionUpdateServiceOrchestrator, times(1))
                .updateTransactionDetailsPaymentStatus(transactionId, statusUpdateDTO, appUser);

    }

    @Test
    void markAllBuyOutSurplusTransactionDetailsPaymentStatus() throws Exception {
        final Long transactionId = 99L;
        final AppUser appUser = AppUser.builder()
                .userId("submitterId")
                .firstName("sub")
                .lastName("mitter")
                .roleType(RoleTypeConstants.REGULATOR)
                .build();
        final BuyOutSurplusTransactionMarkPaymentStatusDTO markPaymentStatusDTO = BuyOutSurplusTransactionMarkPaymentStatusDTO.builder()
                .status(BuyOutSurplusPaymentStatus.PAID)
                .comments("comments")
                .paymentDate(LocalDate.of(2025, 5, 15))
                .build();

        when(appSecurityComponent.getAuthenticatedUser())
                .thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + "/payment-status/mark-all", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(markPaymentStatusDTO)))
                .andExpect(status().isNoContent());

        verify(buyOutSurplusTransactionUpdateServiceOrchestrator, times(1))
                .markTransactionDetailsPaymentStatus(transactionId, markPaymentStatusDTO, appUser);
    }

    @Test
    void updateBuyOutSurplusTransactionReceivedAmount() throws Exception {
        final Long transactionId = 99L;
        final BigDecimal adjustmentAmount = BigDecimal.valueOf(150);
        final LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        final String comments = "bla bla";
        final Map<UUID, String> evidenceFiles = Map.of(UUID.randomUUID(), "fileName");
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .lastName("last")
                .firstName("first")
                .build();
        final BuyOutSurplusTransactionUpdateReceivedAmountDTO updateReceivedAmountDTO = BuyOutSurplusTransactionUpdateReceivedAmountDTO.builder()
                .adjustmentAmount(adjustmentAmount)
                .comments(comments)
                .paymentDate(paymentDate)
                .evidenceFiles(evidenceFiles)
                .build();

        when(appSecurityComponent.getAuthenticatedUser())
                .thenReturn(appUser);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + "/received-amount", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReceivedAmountDTO)))
                .andExpect(status().isNoContent());

        verify(buyOutSurplusTransactionUpdateServiceOrchestrator, times(1))
                .updateTransactionReceivedAmount(transactionId, updateReceivedAmountDTO, appUser);
    }
}