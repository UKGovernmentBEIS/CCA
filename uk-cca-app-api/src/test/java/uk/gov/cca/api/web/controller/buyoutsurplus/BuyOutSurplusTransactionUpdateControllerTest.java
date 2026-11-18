package uk.gov.cca.api.web.controller.buyoutsurplus;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import java.math.BigDecimal;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class BuyOutSurplusTransactionUpdateControllerTest {

	private MockMvc mockMvc;

	private ObjectMapper mapper;

	private static final String CONTROLLER_PATH = "/v1.0/buy-out-surplus/transactions/{id}/update";

	@InjectMocks
	private BuyOutSurplusTransactionUpdateController controller;

	@Mock
	private BuyOutSurplusTransactionService transactionService;

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

		mapper = new ObjectMapper();

		FormattingConversionService conversionService = new FormattingConversionService();

		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
				.setControllerAdvice(new ExceptionControllerAdvice())
				.setConversionService(conversionService)
				.build();
	}

	@Test
	void updateBuyOutSurplusTransactionPaymentStatus() throws Exception {
		final Long transactionId = 99L;

		final String POST = CONTROLLER_PATH.replace("{id}", transactionId.toString()) + "/payment-status";

		final AppUser appUser = AppUser.builder()
				.userId("submitterId")
				.firstName("sub")
				.lastName("mitter")
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		final BuyOutSurplusTransactionUpdatePaymentStatusDTO statusUpdateDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
				.status(BuyOutSurplusPaymentStatus.PAID)
				.comments("comments")
				.paymentDate(LocalDate.of(2025,5,15))
				.build();

		JSONObject statusUpdateJson = new JSONObject();
		statusUpdateJson.put("status", "PAID");
		statusUpdateJson.put("comments", "comments");
		statusUpdateJson.put("paymentDate", "2025-05-15");

		when(appSecurityComponent.getAuthenticatedUser())
				.thenReturn(appUser);

		mockMvc.perform(
						MockMvcRequestBuilders.post(POST)
								.contentType(MediaType.APPLICATION_JSON)
								.param("id", transactionId.toString())
								.content(statusUpdateJson.toString())
				)
				.andDo(print())
				.andExpect(status().isNoContent());

		verify(transactionService, times(1))
				.updateTransactionPaymentStatus(transactionId, statusUpdateDTO, appUser);

	}

	@Test
	void updateBuyOutSurplusTransactionAmount() throws Exception {
		final Long transactionId = 99L;
		final String POST = CONTROLLER_PATH.replace("{id}", transactionId.toString()) + "/amount";

		final BuyOutSurplusTransactionUpdateAmountDTO buyOutFeeUpdateDTO = BuyOutSurplusTransactionUpdateAmountDTO.builder()
				.amount(BigDecimal.valueOf(10.53))
				.comments("comments")
				.build();
		final AppUser appUser = AppUser.builder()
				.userId("submitterId")
				.firstName("sub")
				.lastName("mitter")
				.roleType(RoleTypeConstants.REGULATOR)
				.build();
		when(appSecurityComponent.getAuthenticatedUser())
				.thenReturn(appUser);

		mockMvc.perform(MockMvcRequestBuilders.post(POST)
						.contentType(MediaType.APPLICATION_JSON)
						.param("id", transactionId.toString())
				.content(mapper.writeValueAsString(buyOutFeeUpdateDTO)))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isNoContent());

		verify(transactionService, times(1))
				.updateTransactionAmount(transactionId, buyOutFeeUpdateDTO, appUser);
	}

	@Test
	void updateBuyOutSurplusTransactionPaymentStatus_SpELExpression_throw() throws Exception{
		final Long transactionId = 99L;

		final String POST = CONTROLLER_PATH.replace("{id}", transactionId.toString()) + "/payment-status";

		JSONObject statusUpdateJson = new JSONObject();
		statusUpdateJson.put("status", "PAID");
		statusUpdateJson.put("comments", "comments");

		Exception exception = mockMvc.perform(
						MockMvcRequestBuilders.post(POST)
								.contentType(MediaType.APPLICATION_JSON)
								.param("id", transactionId.toString())
								.content(statusUpdateJson.toString())
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andReturn().getResolvedException();

		assertThat(exception.getClass())
				.isEqualTo(MethodArgumentNotValidException.class);
		Assertions.assertTrue(exception.getMessage().contains("buyOutSurplusTransaction.paymentDate"));
		Assertions.assertTrue(exception.getMessage().contains("SpELExpression"));
	}
}