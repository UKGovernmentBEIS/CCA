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

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailFileEvidenceTokenService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.token.FileToken;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionDetailViewControllerTest {

    @InjectMocks
    private BuyOutSurplusTransactionDetailViewController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Mock
    private BuyOutSurplusTransactionDetailFileEvidenceTokenService buyOutSurplusTransactionFileEvidenceTokenService;

    private MockMvc mockMvc;

    private static final String CONTROLLER_PATH = "/v1.0/buy-out-surplus/transaction-details/{id}";

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (BuyOutSurplusTransactionDetailViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getBuyOutSurplusTransactionDetailHistory() throws Exception {
        final Long transactionDetailId = 99L;

        final AppUser appUser = AppUser.builder().build();
        final List<BuyOutSurplusTransactionDetailHistoryDTO> dtos = List.of(
                BuyOutSurplusTransactionDetailHistoryDTO.builder().id(1L).submitter("regulator").build()
        );

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailHistory(transactionDetailId))
                .thenReturn(dtos);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/history", transactionDetailId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].submitter").value("regulator"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailHistory(transactionDetailId);
    }

    @Test
    void generateBuyOutSurplusTransactionDetailEvidenceFileToken() throws Exception {
        final Long transactionDetailId = 99L;
        final UUID fileEvidenceUuid = UUID.randomUUID();

        final AppUser appUser = AppUser.builder().build();
        final FileToken dto = FileToken.builder().token("token").build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusTransactionFileEvidenceTokenService.generateGetFileEvidenceToken(transactionDetailId, fileEvidenceUuid, appUser))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/evidence", transactionDetailId)
                        .param("fileEvidenceUuid", fileEvidenceUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.token").value("token"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusTransactionFileEvidenceTokenService, times(1))
                .generateGetFileEvidenceToken(transactionDetailId, fileEvidenceUuid, appUser);
    }
}
