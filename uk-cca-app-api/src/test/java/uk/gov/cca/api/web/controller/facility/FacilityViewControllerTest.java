package uk.gov.cca.api.web.controller.facility;

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
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilitySearchCriteria;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationDetailsDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationSearchResultInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilitySearchResults;
import uk.gov.cca.api.web.orchestrator.facility.service.FacilityInfoServiceOrchestrator;
import uk.gov.cca.api.web.orchestrator.facility.service.FacilitySearchServiceOrchestrator;
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
class FacilityViewControllerTest {

    private static final String FACILITIES_CONTROLLER_PATH = "/v1.0/facilities";

    private MockMvc mockMvc;

    @InjectMocks
    private FacilityViewController facilityViewController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private FacilitySearchServiceOrchestrator facilitySearchService;

    @Mock
    private FacilityInfoServiceOrchestrator facilityInfoServiceOrchestrator;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(facilityViewController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        facilityViewController = (FacilityViewController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(facilityViewController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void searchFacilities() throws Exception {
        final Long accountId = 1L;
        final String facilityId = "SA-F00001";
        final String siteName = "site1";
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        final String term = "SA-";
        final int page = 0;
        final int pageSize = 30;

        final FacilityCertificationSearchResultInfoDTO facilityCertificationSearchResultInfoDTO =
                new FacilityCertificationSearchResultInfoDTO(facilityId, siteName, null, FacilityDataStatus.LIVE, FacilityCertificationStatus.CERTIFIED);


        final FacilitySearchCriteria facilitySearchCriteria = FacilitySearchCriteria.builder()
                .term(term)
                .paging(PagingRequest.builder().pageNumber(page).pageSize(pageSize).build())
                .build();

        final FacilitySearchResults facilitySearchResults = FacilitySearchResults.builder()
                .facilities(List.of(facilityCertificationSearchResultInfoDTO))
                .total(1L)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(facilitySearchService.searchFacilities(accountId, facilitySearchCriteria)).thenReturn(facilitySearchResults);

        //invoke
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FACILITIES_CONTROLLER_PATH + "/account/" + accountId)
                        .param("term", facilitySearchCriteria.getTerm())
                        .param("page", String.valueOf(facilitySearchCriteria.getPaging().getPageNumber()))
                        .param("size", String.valueOf(facilitySearchCriteria.getPaging().getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.facilities[0].id").value(facilitySearchResults.getFacilities().get(0).getId()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(facilitySearchService, times(1)).searchFacilities(accountId, facilitySearchCriteria);
    }

    @Test
    void getFacilityDetailsById() throws Exception {

        final String facilityId = "SA-F00001";
        final String siteName = "site1";
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).build();
        final AccountAddressDTO accountAddressDTO = AccountAddressDTO.builder()
                .line1("line1")
                .city("Athens")
                .county("Greece")
                .build();

        FacilityInfoDTO facilityInfoDTO = FacilityInfoDTO.builder()
                .facilityId(facilityId)
                .siteName(siteName)
                .status(FacilityDataStatus.LIVE)
                .address(accountAddressDTO)
                .facilityCertificationDetails(List.of(FacilityCertificationDetailsDTO.builder()
                        .certificationPeriod(CertificationPeriodType.CP6)
                        .status(FacilityCertificationStatus.CERTIFIED)
                        .build()))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(facilityInfoServiceOrchestrator.getFacilityInfo(facilityId)).thenReturn(facilityInfoDTO);

        //invoke
        mockMvc.perform(MockMvcRequestBuilders
                        .get(FACILITIES_CONTROLLER_PATH + "/" + facilityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facilityId").value(facilityInfoDTO.getFacilityId()));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(facilityInfoServiceOrchestrator, times(1)).getFacilityInfo(facilityId);
    }
}
