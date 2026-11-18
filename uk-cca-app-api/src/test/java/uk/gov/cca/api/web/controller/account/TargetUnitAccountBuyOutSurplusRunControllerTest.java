package uk.gov.cca.api.web.controller.account;

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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusExclusionService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class TargetUnitAccountBuyOutSurplusRunControllerTest {

	private static final String REQUEST_PATH = "/v1.0/target-unit-accounts/{accountId}/buy-out-surplus/";

	private ObjectMapper mapper;

	private MockMvc mockMvc;
	@InjectMocks
	private TargetUnitAccountBuyOutSurplusController controller;

	@Mock
	private BuyOutSurplusExclusionService exclusionService;

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
		controller = (TargetUnitAccountBuyOutSurplusController) aopProxy.getProxy();

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
	void updateSurplusGained() throws Exception {

		final long accountId = 999L;
		final String POST_PATH = REQUEST_PATH.replace("{accountId}", String.valueOf(accountId));
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final SurplusUpdateDTO surplusUpdateDTO = SurplusUpdateDTO.builder()
				.newSurplusGained(BigDecimal.TEN)
				.targetPeriodType(targetPeriodType)
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
								.content(mapper.writeValueAsString(surplusUpdateDTO))
						.param("targetPeriodType",TargetPeriodType.TP6.name()))
				.andExpect(status().isNoContent());

		verify(surplusService, times(1))
				.updateSurplusGained(
						surplusUpdateDTO,
						accountId,
						appUser);
	}

	@Test
	void getTargetUnitAccountBuyOutFeesExcluded() throws Exception {

		final long accountId = 999L;
		final String GET_PATH = REQUEST_PATH.replace("{accountId}", String.valueOf(accountId));
		final SurplusGainedDTO surplusGainedDTO = SurplusGainedDTO.builder()
				.surplusGained(BigDecimal.ZERO)
				.targetPeriod(TargetPeriodType.TP6)
				.hasHistory(false).build();
		final List<SurplusGainedDTO> surplusGainedDTOList = List.of(surplusGainedDTO);
		final AccountBuyOutSurplusInfoDTO accountBuyOutSurplusInfoDTO = AccountBuyOutSurplusInfoDTO.builder()
				.excluded(false).surplusGainedDTOList(surplusGainedDTOList).build();
		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(exclusionService.getBuyOutSurplusInfoByAccountId(accountId))
				.thenReturn(accountBuyOutSurplusInfoDTO);

		mockMvc.perform(get(GET_PATH)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.excluded").value(false))
				.andExpect(jsonPath("$.surplusGainedDTOList[0].targetPeriod").value("TP6"))
				.andExpect(jsonPath("$.surplusGainedDTOList[0].surplusGained").value("0"))
				.andExpect(jsonPath("$.surplusGainedDTOList[0].hasHistory").value(false));

		verify(exclusionService, times(1))
				.getBuyOutSurplusInfoByAccountId(accountId);
	}

	@Test
	void getAllSurplusHistoryByTargetPeriodAndAccountId() throws Exception {
		final long accountId = 999L;
		final String GET_PATH = REQUEST_PATH.replace("{accountId}", String.valueOf(accountId)) + "history";

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
		when(surplusQueryService.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId))
				.thenReturn(surplusHistoryDTOS);

		mockMvc.perform(get(GET_PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.param("targetPeriod",TargetPeriodType.TP6.name()))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$[0].submitter").value(surplusHistoryDTOS.get(0).getSubmitter()))
				.andExpect(jsonPath("$[0].comments").value(surplusHistoryDTOS.get(0).getComments()))
				.andExpect(jsonPath("$[0].surplusGained").value(surplusHistoryDTOS.get(0).getSurplusGained()));

		verify(surplusQueryService, times(1))
				.getAllSurplusHistoryByAccountIdAndTargetPeriod(TargetPeriodType.TP6, accountId);
	}

	@Test
	void excludeAccountFromBuyOutSurplus() throws Exception {

		final long accountId = 1L;
		final String POST_PATH = REQUEST_PATH.replace("{accountId}", String.valueOf(accountId)) + "/exclude";

		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		mockMvc.perform(post(POST_PATH))
				.andExpect(status().isNoContent())
				.andDo(MockMvcResultHandlers.print());

		verify(exclusionService, times(1))
				.excludeAccountFromBuyOutSurplus(accountId);
	}

	@Test
	void removeAccountExclusionFromBuyOutSurplus() throws Exception {

		final long accountId = 1L;
		final String DELETE_PATH = REQUEST_PATH.replace("{accountId}", String.valueOf(accountId)) + "/include";
		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		mockMvc.perform(delete(DELETE_PATH))
				.andExpect(status().isNoContent())
				.andDo(MockMvcResultHandlers.print());

		verify(exclusionService, times(1))
				.removeAccountExclusionFromBuyOutSurplus(accountId);
	}
}