package uk.gov.cca.api.web.controller.account;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;

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

import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataAttachmentService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.token.FileToken;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountPerformanceAccountTemplateDataReportControllerTest {

	private static final String CONTROLLER_PATH = "/v1.0/target-unit-accounts/{accountId}/performance-account-template-data-report";

	private MockMvc mockMvc;

	@InjectMocks
	private TargetUnitAccountPerformanceAccountTemplateDataReportController cut;

	@Mock
	private AppSecurityComponent appSecurityComponent;

	@Mock
	private AppUserAuthorizationService appUserAuthorizationService;

	@Mock
	private PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;

	@Mock
	private PerformanceAccountTemplateDataAttachmentService performanceAccountTemplateDataAttachmentService;

	@BeforeEach
	void setUp() {
		AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(
				appSecurityComponent);
		AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

		AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(cut);
		aspectJProxyFactory.addAspect(aspect);

		DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
		AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
		cut = (TargetUnitAccountPerformanceAccountTemplateDataReportController) aopProxy.getProxy();

		FormattingConversionService conversionService = new FormattingConversionService();

		mockMvc = MockMvcBuilders.standaloneSetup(cut)
				.setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
				.setControllerAdvice(new ExceptionControllerAdvice()).setConversionService(conversionService).build();
	}

	@Test
	void getAccountPerformanceDataStatusById_Returns200Ok() throws Exception {
		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		var reportDTO = AccountPerformanceAccountTemplateDataReportInfoDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.build();

		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(performanceAccountTemplateDataQueryService.findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType))
				.thenReturn(Optional.of(reportDTO));

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/info")
				.param("targetPeriodType", targetPeriodType.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.targetPeriodType").value(TargetPeriodType.TP6.name()))
			.andExpect(jsonPath("$.targetPeriodYear").value("2024"))
			.andExpect(jsonPath("$.targetPeriodName").value("test"));

		verify(performanceAccountTemplateDataQueryService, times(1))
				.findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType);
	}
	
	@Test
	void getAccountPerformanceAccountTemplateDataReportDetails_Returns200Ok() throws Exception {
		final Long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		var reportDTO = AccountPerformanceAccountTemplateDataReportDetailsDTO.builder()
				.targetPeriodYear(Year.of(2024))
				.targetPeriodType(TargetPeriodType.TP6)
				.targetPeriodName("test")
				.data(PerformanceAccountTemplateDataContainer.builder().file(FileInfoDTO.builder().name("dfd").build()).build())
				.build();

		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(performanceAccountTemplateDataQueryService.getReportDetailsByAccountIdAndTargetPeriod(accountId, targetPeriodType))
				.thenReturn(reportDTO);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/details")
				.param("targetPeriodType", targetPeriodType.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.targetPeriodType").value(TargetPeriodType.TP6.name()))
			.andExpect(jsonPath("$.targetPeriodYear").value("2024"))
			.andExpect(jsonPath("$.targetPeriodName").value("test"));

		verify(performanceAccountTemplateDataQueryService, times(1))
				.getReportDetailsByAccountIdAndTargetPeriod(accountId, targetPeriodType);
	}
	
	@Test
	void generateGetAccountPerformanceAccountTemplateDataReportAttachmentToken() throws Exception {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		UUID fileAttachmentUuid = UUID.randomUUID();
		FileToken token = FileToken.builder().token("token").build();

		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(performanceAccountTemplateDataAttachmentService
				.generateGetAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid))
				.thenReturn(token);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{accountId}", accountId.toString()) + "/attachment-token")
				.param("targetPeriodType", targetPeriodType.toString())
				.param("fileAttachmentUuid", fileAttachmentUuid.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").value(token.getToken()));

		verify(performanceAccountTemplateDataAttachmentService, times(1))
				.generateGetAttachmentToken(accountId,
				targetPeriodType, fileAttachmentUuid);
	}
	
}
