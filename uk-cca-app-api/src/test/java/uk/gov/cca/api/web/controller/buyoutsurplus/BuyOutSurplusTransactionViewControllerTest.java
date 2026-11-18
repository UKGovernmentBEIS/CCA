package uk.gov.cca.api.web.controller.buyoutsurplus;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionSummaryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDocumentService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionFileEvidenceTokenService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.token.FileToken;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionViewControllerTest {

    @InjectMocks
    private BuyOutSurplusTransactionViewController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private BuyOutSurplusTransactionDocumentService buyOutSurplusTransactionDocumentService;

    @Mock
    private BuyOutSurplusTransactionFileEvidenceTokenService buyOutSurplusTransactionFileEvidenceTokenService;

    private MockMvc mockMvc;

    private static final String CONTROLLER_PATH = "/v1.0/buy-out-surplus/transactions/{id}";

    @BeforeEach
    void setUp() {

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (BuyOutSurplusTransactionViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getBuyOutSurplusTransactionSummary() throws Exception {
        final Long transactionId = 99L;

        final AppUser appUser = AppUser.builder().build();
        final BuyOutSurplusTransactionSummaryDTO dto = BuyOutSurplusTransactionSummaryDTO.builder()
                .transactionCode("TRX12345")
                .currentAmount(BigDecimal.TEN)
                .initialAmount(BigDecimal.TWO)
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusQueryService.getBuyOutSurplusTransactionSummary(transactionId)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH, transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.transactionCode").value("TRX12345"))
                .andExpect(jsonPath("$.currentAmount").value(BigDecimal.TEN))
                .andExpect(jsonPath("$.initialAmount").value(BigDecimal.TWO))
                .andExpect(jsonPath("$.paymentStatus").value(BuyOutSurplusPaymentStatus.PAID.name()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusQueryService, times(1)).getBuyOutSurplusTransactionSummary(transactionId);
    }

    @Test
    void getBuyOutSurplusTransactionDetails() throws Exception {
        final Long transactionId = 99L;

        final AppUser appUser = AppUser.builder().build();
        final BuyOutSurplusTransactionDetailsDTO dto = BuyOutSurplusTransactionDetailsDTO.builder()
                .transactionCode("TRX12345")
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusQueryService.getBuyOutSurplusTransactionDetails(transactionId)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/details", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.transactionCode").value("TRX12345"))
                .andExpect(jsonPath("$.paymentStatus").value(BuyOutSurplusPaymentStatus.PAID.name()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusQueryService, times(1)).getBuyOutSurplusTransactionDetails(transactionId);
    }

    @Test
    void generateBuyOutSurplusTransactionDocumentToken() throws Exception {
        final Long transactionId = 99L;
        final UUID fileDocumentUuid = UUID.randomUUID();

        final AppUser appUser = AppUser.builder().build();
        final FileToken dto = FileToken.builder().token("token").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusTransactionDocumentService.generateGetFileDocumentToken(transactionId, fileDocumentUuid)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/document", transactionId)
                        .param("fileDocumentUuid", fileDocumentUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.token").value("token"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusTransactionDocumentService, times(1))
                .generateGetFileDocumentToken(transactionId, fileDocumentUuid);
    }

    @Test
    void getBuyOutSurplusTransactionHistory() throws Exception {
        final Long transactionId = 99L;

        final AppUser appUser = AppUser.builder().build();
        final List<BuyOutSurplusTransactionHistoryDTO> dtos = List.of(
                BuyOutSurplusTransactionHistoryDTO.builder().id(1L).submitter("regulator").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusQueryService.getBuyOutSurplusTransactionHistory(transactionId)).thenReturn(dtos);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/history", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].submitter").value("regulator"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusQueryService, times(1)).getBuyOutSurplusTransactionHistory(transactionId);
    }

    @Test
    void generateBuyOutSurplusTransactionEvidenceFileToken() throws Exception {
        final Long transactionId = 99L;
        final UUID fileEvidenceUuid = UUID.randomUUID();

        final AppUser appUser = AppUser.builder().build();
        final FileToken dto = FileToken.builder().token("token").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusTransactionFileEvidenceTokenService.generateGetFileEvidenceToken(transactionId, fileEvidenceUuid, appUser))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/evidence", transactionId)
                        .param("fileEvidenceUuid", fileEvidenceUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.token").value("token"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusTransactionFileEvidenceTokenService, times(1))
                .generateGetFileEvidenceToken(transactionId, fileEvidenceUuid, appUser);
    }
}
