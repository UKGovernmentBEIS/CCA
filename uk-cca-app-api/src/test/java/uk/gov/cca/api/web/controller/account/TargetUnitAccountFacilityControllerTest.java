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

import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.account.service.TargetUnitAccountFacilitySearchServiceOrchestrator;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResultExtendedDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResults;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountFacilityControllerTest {

    private static final String FACILITIES_CONTROLLER_PATH = "/v1.0/target-unit-accounts/{accountId}/facilities";

    private MockMvc mockMvc;

    @InjectMocks
    private TargetUnitAccountFacilityController targetUnitAccountFacilityController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private TargetUnitAccountFacilitySearchServiceOrchestrator facilitySearchService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(targetUnitAccountFacilityController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        targetUnitAccountFacilityController = (TargetUnitAccountFacilityController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(targetUnitAccountFacilityController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void searchFacilities() throws Exception {
        final Long accountId = 1L;
        final String facilityBusinessId = "SA-F00001";
        final String siteName = "site1";
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        final String term = "SA-";
        final int page = 0;
        final int pageSize = 30;

        final FacilitySearchResultExtendedDTO facilitySearchResultExtendedDTO =
                new FacilitySearchResultExtendedDTO(1L, facilityBusinessId, siteName, null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED, null);


        final FacilitySearchCriteria facilitySearchCriteria = FacilitySearchCriteria.builder()
                .term(term)
                .paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
                .build();

        final FacilitySearchResults facilitySearchResults = FacilitySearchResults.builder()
                .facilities(List.of(facilitySearchResultExtendedDTO))
                .total(1L)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(facilitySearchService.searchFacilities(accountId, user, facilitySearchCriteria)).thenReturn(facilitySearchResults);

        // Invoke
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FACILITIES_CONTROLLER_PATH, accountId)
                        .param("term", facilitySearchCriteria.getTerm())
                        .param("page", String.valueOf(facilitySearchCriteria.getPaging().getPageNumber()))
                        .param("size", String.valueOf(facilitySearchCriteria.getPaging().getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.facilities[0].id").value(facilitySearchResults.getFacilities().getFirst().getId()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(facilitySearchService, times(1)).searchFacilities(accountId, user, facilitySearchCriteria);
    }
}
