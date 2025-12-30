package uk.gov.cca.api.web.controller.account;

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
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusAttachmentService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.token.FileToken;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountPerformanceDataReportControllerTest {

	private static final String CONTROLLER_PATH = "/v1.0/target-unit-accounts/{accountId}/performance-data-report";

	private MockMvc mockMvc;

	@InjectMocks
	private TargetUnitAccountPerformanceDataReportController controller;

	@Mock
	private AccountPerformanceDataStatusQueryService statusQueryService;

	@Mock
	private AccountPerformanceDataStatusService statusService;

	@Mock
	private AccountPerformanceDataStatusAttachmentService statusAttachmentService;

	@Mock
	private AppSecurityComponent appSecurityComponent;

	@Mock
	private AppUserAuthorizationService appUserAuthorizationService;

	@BeforeEach
	void setUp() {
		AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(
				appSecurityComponent);
		AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

		AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
		aspectJProxyFactory.addAspect(aspect);

		DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
		AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
		controller = (TargetUnitAccountPerformanceDataReportController) aopProxy.getProxy();

		FormattingConversionService conversionService = new FormattingConversionService();

		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
				.setControllerAdvice(new ExceptionControllerAdvice()).setConversionService(conversionService).build();
	}

	@Test
	void getAccountPerformanceDataStatusById_Returns200Ok() throws Exception {
		final Long accountId = 912L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();

		final AccountPerformanceDataStatusInfoDTO mockDto = AccountPerformanceDataStatusInfoDTO.builder().locked(true)
				.reportVersion(2).targetPeriodName("TP6 (2024)").isEditable(true).targetPeriodType(targetPeriodType)
				.build();

		when(statusQueryService.getAccountPerformanceDataStatusInfo(accountId, targetPeriodType,
				currentUser)).thenReturn(mockDto);
		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/status")
				.param("targetPeriodType", targetPeriodType.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$.locked").value(true))
				.andExpect(jsonPath("$.reportVersion").value(2))
			.andExpect(jsonPath("$.targetPeriodName").value("TP6 (2024)"));

		verify(statusQueryService, times(1)).getAccountPerformanceDataStatusInfo(accountId,
				targetPeriodType, currentUser);

	}

	@Test
	void getAccountPerformanceDataStatusById_Returns403Forbidden() throws Exception {
		final Long accountId = 912L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(statusQueryService)
				.getAccountPerformanceDataStatusInfo(accountId, targetPeriodType, user);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/status")
				.param("targetPeriodType", targetPeriodType.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden()).andDo(print());
	}

	@Test
	void updateAccountPerformanceDataStatusLock_Returns204NoContent() throws Exception {
		// Arrange
		final Long accountId = 912L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

		AccountPerformanceDataUpdateLockDTO updateLockDTO = AccountPerformanceDataUpdateLockDTO.builder().locked(true)
				.targetPeriodType(targetPeriodType).build();
		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		// Act & Assert
		mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/lock")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"locked\":true,\"targetPeriodType\":\"TP6\"}"))
			.andExpect(status().isNoContent())
			.andDo(print());

		// Verify service interaction
		verify(statusService, times(1)).updateAccountPerformanceDataLock(accountId,
				updateLockDTO);
	}

	@Test
	void getPerformanceDataDetails_Returns200Ok() throws Exception {
		final Long accountId = 912L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

		final AccountPerformanceDataReportDetailsDTO mockDto = AccountPerformanceDataReportDetailsDTO.builder()
				.tpOutcome(TargetPeriodResultType.TARGET_MET).build();

		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(statusQueryService.getAccountPerformanceDataReportDetails(accountId, targetPeriodType))
				.thenReturn(mockDto);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/details")
				.param("targetPeriodType", targetPeriodType.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"tpOutcome\": \"TARGET_MET\"}"))
			.andExpect(status().isOk())
			.andDo(print());

		verify(statusQueryService, times(1)).getAccountPerformanceDataReportDetails(accountId,
				targetPeriodType);

	}

	@Test
	void generateGetAccountPerformanceDataReportAttachmentToken() throws Exception {
		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		UUID fileAttachmentUuid = UUID.randomUUID();
		FileToken expectedToken = FileToken.builder().token("token").build();

		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(statusAttachmentService.generateGetAccountPerformanceDataReportAttachmentToken(accountId, targetPeriodType,
				fileAttachmentUuid)).thenReturn(expectedToken);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/attachment")
				.param("targetPeriodType", targetPeriodType.toString())
				.param("fileAttachmentUuid", fileAttachmentUuid.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value(expectedToken.getToken()));

		verify(statusAttachmentService, times(1)).generateGetAccountPerformanceDataReportAttachmentToken(accountId,
				targetPeriodType, fileAttachmentUuid);
	}

	@Test
	void generateGetAccountPerformanceDataReportAttachmentToken_Returns403Forbidden() throws Exception {
		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		UUID fileAttachmentUuid = UUID.randomUUID();
		final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN))
			.when(appUserAuthorizationService)
			.authorize(user, "generateGetAccountPerformanceDataReportAttachmentToken", String.valueOf(accountId), null, null);
		
		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/attachment")
				.param("targetPeriodType", targetPeriodType.toString())
				.param("fileAttachmentUuid", fileAttachmentUuid.toString()))
			.andExpect(status().isForbidden());


		verifyNoInteractions(statusAttachmentService);
	}
}