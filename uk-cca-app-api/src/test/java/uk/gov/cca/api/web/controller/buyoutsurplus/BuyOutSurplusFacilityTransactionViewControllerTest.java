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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionReceivedAmountHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailSearchResultsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDetailsSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionDetailQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionQueryService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityTransactionViewControllerTest {

    @InjectMocks
    private BuyOutSurplusFacilityTransactionViewController controller;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private BuyOutSurplusTransactionDetailQueryService buyOutSurplusTransactionDetailQueryService;

    @Mock
    private BuyOutSurplusTransactionQueryService buyOutSurplusTransactionQueryService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String CONTROLLER_PATH = "/v1.0/buy-out-surplus/facility-transactions/{id}";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (BuyOutSurplusFacilityTransactionViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getFacilityBuyOutSurplusTransaction() throws Exception {
        final Long transactionId = 99L;

        final AppUser appUser = AppUser.builder().build();
        final FacilityBuyOutSurplusTransactionDTO dto = FacilityBuyOutSurplusTransactionDTO.builder()
                .transactionCode("TRX12345")
                .paymentStatus(BuyOutSurplusPaymentStatus.PAID)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusQueryService.getFacilityBuyOutSurplusTransaction(transactionId)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH, transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.transactionCode").value("TRX12345"))
                .andExpect(jsonPath("$.paymentStatus").value(BuyOutSurplusPaymentStatus.PAID.name()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusQueryService, times(1)).getFacilityBuyOutSurplusTransaction(transactionId);
    }

    @Test
    void getFacilityBuyOutSurplusTransactionDetails() throws Exception {
        final Long transactionId = 99L;

        final AppUser appUser = AppUser.builder().build();
        final FacilityBuyOutSurplusTransactionDetailsSearchCriteria criteria = FacilityBuyOutSurplusTransactionDetailsSearchCriteria.builder()
                .paging(PagingRequest.builder().pageSize(5).pageNumber(0).build())
                .build();
        final FacilityBuyOutSurplusTransactionDetailSearchResultsDTO dto = FacilityBuyOutSurplusTransactionDetailSearchResultsDTO.builder()
                .transactionDetails(List.of(FacilityBuyOutSurplusTransactionDetailSearchResultInfoDTO.builder().id(1L).build()))
                .total(1L)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusTransactionDetailQueryService.getBuyOutSurplusTransactionDetailsList(transactionId, criteria))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + "/details", transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criteria)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.transactionDetails[0].id").value(1));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusTransactionDetailQueryService, times(1))
                .getBuyOutSurplusTransactionDetailsList(transactionId, criteria);
    }

    @Test
    void getBuyOutSurplusTransactionReceivedAmountHistory() throws Exception {
        final Long transactionId = 99L;
        final AppUser appUser = AppUser.builder().build();
        LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        LocalDateTime submissionDate = LocalDate.of(2026, 6, 5).atStartOfDay();
        String submitter = "submitter";
        String comments = "bla bla";
        Long historyId = 34L;

        final BuyOutSurplusTransactionReceivedAmountHistoryDTO historyDTO = BuyOutSurplusTransactionReceivedAmountHistoryDTO.builder()
                .id(historyId)
                .paymentDate(paymentDate)
                .adjustmentAmount(BigDecimal.valueOf(100))
                .submissionDate(submissionDate)
                .submitter(submitter)
                .evidenceFiles(Map.of())
                .comments(comments)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        when(buyOutSurplusTransactionQueryService.getBuyOutSurplusTransactionReceivedAmountHistory(transactionId))
                .thenReturn(List.of(historyDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH + "/received-amount/history", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.[0].id").value(historyId));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(buyOutSurplusTransactionQueryService, times(1)).getBuyOutSurplusTransactionReceivedAmountHistory(transactionId);
    }
}
