package uk.gov.cca.api.web.controller.sectorassociation;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.authorization.ccaauth.core.domain.AppCcaAuthority;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.user.core.domain.UserBasicInfoDTO;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationDetailsResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.service.SectorAssociationQueryServiceOrchestrator;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith({MockitoExtension.class})
class SectorAssociationViewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private SectorAssociationQueryServiceOrchestrator sectorAssociationQueryServiceOrchestrator;

    @InjectMocks
    private SectorAssociationViewController sectorAssociationViewController;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        final AopProxy aopProxy = getAopProxy(authorizationAspectUserResolver);

        sectorAssociationViewController = (SectorAssociationViewController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(sectorAssociationViewController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getSectorAssociationById() throws Exception {
        final AppUser regulatorUser = getAppUser(REGULATOR);
        Long sectorAssociationId = 1L;
        SectorAssociationResponseDTO sectorAssociationResponseDTO = createSectorAssociationResponseDTO();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(regulatorUser);
        when(sectorAssociationQueryServiceOrchestrator.getSectorAssociationById(sectorAssociationId, regulatorUser)).thenReturn(sectorAssociationResponseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1.0/sector-association/" + sectorAssociationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(sectorAssociationQueryServiceOrchestrator, times(1)).getSectorAssociationById(sectorAssociationId, regulatorUser);
    }

    @Test
    void getSectorAssociations_whenRegulator() throws Exception {

        final AppUser regulatorUser = getAppUser(REGULATOR);

        List<SectorAssociationInfoDTO> sectorAssociations = createSectorAssociationInfoList();

        //mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(regulatorUser);
        when(sectorAssociationQueryServiceOrchestrator.getSectorAssociations(regulatorUser)).thenReturn(sectorAssociations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1.0/sector-association/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(sectorAssociations.size())))
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder(1, 2)));

        verify(sectorAssociationQueryServiceOrchestrator, times(1)).getSectorAssociations(regulatorUser);
    }

    @Test
    void getSectorAssociations_whenSectorUser() throws Exception {

        final AppUser sectorUser = AppUser.builder()
                .roleType(SECTOR_USER)
                .authorities(List.of(AppCcaAuthority.builder().sectorAssociationId(1L).build()))
                .build();

        // sector user has authority only for sector with id = 1L
        List<SectorAssociationInfoDTO> sectorAssociations = createSectorAssociationInfoList().subList(0, 1);

        //mock
        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(sectorUser);
        when(sectorAssociationQueryServiceOrchestrator.getSectorAssociations(sectorUser)).thenReturn(sectorAssociations);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1.0/sector-association/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(sectorAssociations.size())))
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder(1)));

        verify(sectorAssociationQueryServiceOrchestrator, times(1)).getSectorAssociations(sectorUser);
    }

    private static SectorAssociationResponseDTO createSectorAssociationResponseDTO() {
        return SectorAssociationResponseDTO.builder()
            .sectorAssociationContact(SectorAssociationContactDTO.builder()
                .title("Mr.")
                .firstName("John")
                .lastName("Doe")
                .jobTitle("Director")
                .organisationName("Acme Corp")
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .build())
            .sectorAssociationDetails(SectorAssociationDetailsResponseDTO.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .legalName("Some Association Legal")
                .commonName("Some Association")
                .acronym("SA")
                .facilitator(UserBasicInfoDTO.builder()
                    .firstName("firstName")
                    .lastName("lastName")
                    .build())
                .energyIntensiveOrEPR("Energy Factor")
                .noticeServiceAddress(AddressDTO.builder()
                    .postcode("12345")
                    .line1("123 Main St")
                    .line2("124 Second St")
                    .city("Springfield")
                    .county("CountyName")
                    .build())
                .build())
            .build();
    }

    private static List<SectorAssociationInfoDTO> createSectorAssociationInfoList() {
        final SectorAssociationInfoDTO.SectorAssociationInfoDTOBuilder builder = SectorAssociationInfoDTO.builder();
        final SectorAssociationInfoDTO sector1 = builder.id(1L).sector("ADS - Aerospace").mainContact("William MacDonald").build();
        final SectorAssociationInfoDTO sector2 = builder.id(2L).sector("AFED - Aluminium").mainContact("Sharon McBride").build();
        return List.of(sector1, sector2);
    }

    @NotNull
    private AopProxy getAopProxy(AuthorizationAspectUserResolver authorizationAspectUserResolver) {
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(sectorAssociationViewController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        return proxyFactory.createAopProxy(aspectJProxyFactory);
    }

    private AppUser getAppUser(String roleType) {
        return switch (roleType) {
            case REGULATOR -> {
                final CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;

                yield AppUser.builder()
                        .roleType(REGULATOR)
                        .authorities(List.of(AppAuthority.builder().competentAuthority(ca).build()))
                        .build();
            }
            case SECTOR_USER -> AppUser.builder()
                        .roleType(SECTOR_USER)
                        .authorities(List.of(AppCcaAuthority.builder().sectorAssociationId(1L).build()))
                        .build();
            default -> AppUser.builder()
                    .roleType(OPERATOR)
                    .authorities(List.of(AppAuthority.builder().accountId(1L).build()))
                    .build();
        };
    }
}
