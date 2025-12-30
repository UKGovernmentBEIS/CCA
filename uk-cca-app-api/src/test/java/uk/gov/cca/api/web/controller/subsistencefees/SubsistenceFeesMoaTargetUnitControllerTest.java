package uk.gov.cca.api.web.controller.subsistencefees;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilitySearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFacilityMarkingStatusService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFacilityQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaTargetUnitQueryService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaTargetUnitControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/subsistence-fees/moa-target-units/";

    private MockMvc mockMvc;

    @InjectMocks
    private SubsistenceFeesMoaTargetUnitController subsistenceFeesMoaTargetUnitController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private SubsistenceFeesMoaTargetUnitQueryService subsistenceFeesMoaTargetUnitQueryService;

    @Mock
    private SubsistenceFeesMoaFacilityQueryService subsistenceFeesMoaFacilityQueryService;

    @Mock
    private SubsistenceFeesMoaFacilityMarkingStatusService markingFacilitiesStatusService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(subsistenceFeesMoaTargetUnitController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        subsistenceFeesMoaTargetUnitController = (SubsistenceFeesMoaTargetUnitController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(subsistenceFeesMoaTargetUnitController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getSubsistenceFeesMoaTargetUnitDetailsById() throws Exception {
        Long moaTargetUnitId = 1L;
        LocalDateTime date = LocalDateTime.now();

        SubsistenceFeesMoaTargetUnitDetailsDTO sfrMoaTargetUnitDetailsDTO = new SubsistenceFeesMoaTargetUnitDetailsDTO(1L,
                "businessId", "name", BigDecimal.valueOf(1000L), date, BigDecimal.valueOf(185L), BigDecimal.valueOf(1000L), 10L, 10L);
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(subsistenceFeesMoaTargetUnitQueryService.getSubsistenceFeesMoaTargetUnitDetailsById(moaTargetUnitId)).thenReturn(sfrMoaTargetUnitDetailsDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + moaTargetUnitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businessId").value("businessId"));

        verify(subsistenceFeesMoaTargetUnitQueryService, times(1)).getSubsistenceFeesMoaTargetUnitDetailsById(moaTargetUnitId);
    }

    @Test
    void getSubsistenceFeesMoaTargetUnitDetailsById_forbidden() throws Exception {
        Long moaTargetUnitId = 1L;
        AppUser appUser = AppUser.builder()
                .userId("userId")
                .roleType(SECTOR_USER)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getSubsistenceFeesMoaTargetUnitDetailsById", Long.toString(moaTargetUnitId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + moaTargetUnitId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(subsistenceFeesMoaTargetUnitQueryService);
    }

    @Test
    void getSubsistenceFeesMoaFacilities() throws Exception {
        final AppUser user = AppUser.builder().roleType(REGULATOR).build();
        final int page = 0;
        final int pageSize = 30;
        final long moaTargetUnitId = 1L;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
                .paging(pagingRequest)
                .build();
        SubsistenceFeesMoaFacilitySearchResultInfoDTO dto =
                new SubsistenceFeesMoaFacilitySearchResultInfoDTO(1L, "ADS-001", "site_name", FacilityPaymentStatus.IN_PROGRESS, null, false);
        SubsistenceFeesMoaFacilitySearchResults results = SubsistenceFeesMoaFacilitySearchResults.builder()
                .subsistenceFeesMoaFacilities(List.of(dto))
                .total(1L)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(subsistenceFeesMoaFacilityQueryService.getSubsistenceFeesMoaFacilities(moaTargetUnitId, criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + moaTargetUnitId + "/facilities")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.subsistenceFeesMoaFacilities[0].moaFacilityId").value(1L))
                .andExpect(jsonPath("$.subsistenceFeesMoaFacilities[0].facilityBusinessId").value("ADS-001"))
                .andExpect(jsonPath("$.subsistenceFeesMoaFacilities[0].facilityName").value("site_name"))
                .andExpect(jsonPath("$.subsistenceFeesMoaFacilities[0].hasHistory").value("false"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(subsistenceFeesMoaFacilityQueryService, times(1)).getSubsistenceFeesMoaFacilities(moaTargetUnitId, criteria);
    }

    @Test
    void getSubsistenceFeesMoaFacilities_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).userId("userId").build();
        final int page = 0;
        final int pageSize = 30;
        final long moaTargetUnitId = 1L;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
                .paging(pagingRequest)
                .build();

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getSubsistenceFeesMoaFacilities", String.valueOf(1L), null, null);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + moaTargetUnitId + "/facilities")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(subsistenceFeesMoaFacilityQueryService, never()).getSubsistenceFeesMoaFacilities(moaTargetUnitId, criteria);
    }

    @Test
    void markFacilitiesStatusByMoaTargetUnitId() throws Exception {
        Long moaTargetUnitId = 1L;
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();

        SubsistenceFeesMoaFacilityMarkingStatusDTO statusDTO = SubsistenceFeesMoaFacilityMarkingStatusDTO.builder()
                .filterResourceIds(Set.of(1L, 2L, 3L))
                .status(FacilityPaymentStatus.COMPLETED)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(submitter);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + moaTargetUnitId + "/facilities/mark")
                        .content(mapper.writeValueAsString(statusDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(markingFacilitiesStatusService, times(1)).markMoaFacilitiesStatusByTargetUnitId(submitter, moaTargetUnitId, statusDTO);
    }
}
