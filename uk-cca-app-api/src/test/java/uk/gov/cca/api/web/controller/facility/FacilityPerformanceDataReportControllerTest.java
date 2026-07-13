package uk.gov.cca.api.web.controller.facility;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.time.Year;
import java.util.List;

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

import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.FacilityPerformanceDataUpdateVariationIndicatorDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
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

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceDataReportControllerTest {

	private static final String CONTROLLER_PATH = "/v1.0/facilities/{facilityId}/performance-data-report";

	private MockMvc mockMvc;

	@InjectMocks
	private FacilityPerformanceDataReportController controller;

	@Mock
	private PerformanceDataFacilityStatusQueryService statusQueryService;
	
	@Mock
	private PerformanceDataFacilityStatusService statusService;

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
		controller = (FacilityPerformanceDataReportController) aopProxy.getProxy();

		FormattingConversionService conversionService = new FormattingConversionService();
		
		conversionService.addConverter(String.class, Year.class, Year::parse);

		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
				.setControllerAdvice(new ExceptionControllerAdvice()).setConversionService(conversionService).build();
	}

	@Test
	void getFacilityPerformanceDataStatusById_Ok() throws Exception {
		final Long facilityId = 912L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
		AppUser currentUser = AppUser.builder().roleType(REGULATOR).build();

		final List<FacilityPerformanceDataStatusInfoDTO> mockDtoList = List.of(FacilityPerformanceDataStatusInfoDTO.builder()
				.locked(true).reportVersion(2).targetPeriodName("TP7 (2026)").targetPeriodType(targetPeriodType)
				.build());

		when(statusQueryService.getFacilityPerformanceDataStatusInfo(facilityId, targetPeriodType, 
				PerformanceDataReportType.FINAL, currentUser)).thenReturn(mockDtoList);
		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/status")
				.param("targetPeriodType", targetPeriodType.toString())
				.param("reportType", PerformanceDataReportType.FINAL.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].locked").value(true))
			.andExpect(jsonPath("$[0].reportVersion").value(2))
			.andExpect(jsonPath("$[0].targetPeriodName").value("TP7 (2026)"));

		verify(statusQueryService, times(1)).getFacilityPerformanceDataStatusInfo(facilityId, targetPeriodType,
				PerformanceDataReportType.FINAL, currentUser);

	}

	@Test
	void getFacilityPerformanceDataStatusById_Forbidden() throws Exception {
		final Long facilityId = 912L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
		final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(statusQueryService)
				.getFacilityPerformanceDataStatusInfo(facilityId, targetPeriodType, PerformanceDataReportType.FINAL, user);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/status")
				.param("targetPeriodType", targetPeriodType.toString())
				.param("reportType", PerformanceDataReportType.FINAL.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden()).andDo(print());
	}
	
	@Test
	void getFacilityPerformanceDataReportDetails_Ok() throws Exception {
		final Long facilityId = 912L;
		final Year targetPeriodYear = Year.of(2027);

		final FacilityPerformanceDataReportDetailsDTO mockDto = FacilityPerformanceDataReportDetailsDTO.builder()
				.targetPeriod(TargetPeriodType.TP7).build();

		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(statusQueryService.getFacilityPerformanceDataReportDetails(facilityId, targetPeriodYear))
				.thenReturn(mockDto);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/details")
				.param("targetPeriodYear", targetPeriodYear.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"targetPeriod\": \"TP7\"}"))
			.andExpect(status().isOk())
			.andDo(print());

		verify(statusQueryService, times(1)).getFacilityPerformanceDataReportDetails(facilityId, targetPeriodYear);
	}
	
	@Test
	void getFacilityPerformanceDataReportDetails_Forbidden() throws Exception {
		final Long facilityId = 912L;
		final Year targetPeriodYear = Year.of(2027);
		final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(statusQueryService)
				.getFacilityPerformanceDataReportDetails(facilityId, targetPeriodYear);

		mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/details")
				.param("targetPeriodYear", targetPeriodYear.toString())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden()).andDo(print());
	}
	
	@Test
	void updateFacilityPerformanceDataStatusLock_Ok() throws Exception {
		final Long facilityId = 912L;
		final Year targetPeriodYear = Year.of(2027);
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

		FacilityPerformanceDataUpdateLockDTO updateLockDTO = FacilityPerformanceDataUpdateLockDTO.builder()
				.locked(true)
				.targetPeriodYear(targetPeriodYear)
				.targetPeriodType(targetPeriodType)
				.build();
		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/lock")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"locked\":true,\"targetPeriodYear\":\"2027\",\"targetPeriodType\":\"TP7\"}"))
			.andExpect(status().isNoContent())
			.andDo(print());

		verify(statusService, times(1)).updateFacilityPerformanceDataLock(facilityId,
				updateLockDTO);
	}
	
	@Test
	void updateFacilityPerformanceDataStatusLock_Forbidden() throws Exception {
		final Long facilityId = 912L;
		final Year targetPeriodYear = Year.of(2027);
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
		FacilityPerformanceDataUpdateLockDTO updateLockDTO = FacilityPerformanceDataUpdateLockDTO.builder()
				.locked(true)
				.targetPeriodYear(targetPeriodYear)
				.targetPeriodType(targetPeriodType)
				.build();
		final AppUser user = AppUser.builder().roleType(RoleTypeConstants.OPERATOR).build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(statusService)
				.updateFacilityPerformanceDataLock(facilityId, updateLockDTO);

		mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/lock")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"locked\":true,\"targetPeriodYear\":\"2027\",\"targetPeriodType\":\"TP7\"}"))
			.andExpect(status().isForbidden()).andDo(print());
	}
	
	@Test
	void updateFacilityPerformanceDataStatusVariationIndicator_Ok() throws Exception {
		final Long facilityId = 912L;
		final Year targetPeriodYear = Year.of(2027);

		FacilityPerformanceDataUpdateVariationIndicatorDTO updateDTO = FacilityPerformanceDataUpdateVariationIndicatorDTO.builder()
				.variationIndicator(true)
				.targetPeriodYear(targetPeriodYear)
				.build();
		final AppUser user = AppUser.builder()
				.roleType(RoleTypeConstants.REGULATOR)
				.build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

		mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/variation-indicator")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"variationIndicator\":true,\"targetPeriodYear\":\"2027\"}"))
			.andExpect(status().isNoContent())
			.andDo(print());

		verify(statusService, times(1)).updateFacilityPerformanceDataVariationIndicator(facilityId, updateDTO);
	}
	
	@Test
	void updateFacilityPerformanceDataStatusVariationIndicator_Forbidden() throws Exception {
		final Long facilityId = 912L;
		final Year targetPeriodYear = Year.of(2027);
		FacilityPerformanceDataUpdateVariationIndicatorDTO updateDTO = FacilityPerformanceDataUpdateVariationIndicatorDTO.builder()
				.variationIndicator(true)
				.targetPeriodYear(targetPeriodYear)
				.build();
		final AppUser user = AppUser.builder().roleType(CcaRoleTypeConstants.SECTOR_USER).build();

		when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(statusService)
				.updateFacilityPerformanceDataVariationIndicator(facilityId, updateDTO);

		mockMvc.perform(MockMvcRequestBuilders.put(CONTROLLER_PATH.replace("{facilityId}", facilityId.toString()) + "/variation-indicator")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"variationIndicator\":true,\"targetPeriodYear\":\"2027\"}"))
			.andExpect(status().isForbidden()).andDo(print());
	}
}
