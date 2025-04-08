package uk.gov.cca.api.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserInvitationService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
class SectorUserInvitationControllerTest {

    public static final String SECTOR_USER_CONTROLLER_REGISTRATION_BASE_PATH = "/v1.0/sector-users/invite";
    public static final String ADD_TO_SECTOR_ASSOCIATION_PATH = "/sector-association";
    private static final String SECTOR_USER_EMAIL = "sector_user_email";
    private static final String SECTOR_USER_FNAME = "sector_user_fname";
    private static final String SECTOR_USER_LNAME = "sector_user_lname";
    private static final String SECTOR_ROLE_CODE = "sector_user_administrator";

    @InjectMocks
    private SectorUserInvitationController sectorUserInvitationController;

    @Mock
    private SectorUserInvitationService sectorUserInvitationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private AppUserArgumentResolver appUserArgumentResolver;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(sectorUserInvitationController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        sectorUserInvitationController = (SectorUserInvitationController) aopProxy.getProxy();
        mapper = new ObjectMapper();
        appUserArgumentResolver = Mockito.mock(AppUserArgumentResolver.class);
        validator = Mockito.mock(Validator.class);

        mockMvc = MockMvcBuilders.standaloneSetup(sectorUserInvitationController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setCustomArgumentResolvers(appUserArgumentResolver)
                .setValidator(validator)
                .build();
    }

    @Test
    void inviteSectorUserToSectorAssociation() throws Exception {
        AppUser currentUser = AppUser.builder().userId("user_id").roleType(SECTOR_USER).build();
        SectorUserInvitationDTO sectorUserInvitationDTO = buildMockSectorUserInvitationDTO();
        Long sectorAssociationId = 1L;

        when(appUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(appUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(currentUser);
        doNothing().when(sectorUserInvitationService).inviteUserToSectorAssociation(sectorAssociationId, sectorUserInvitationDTO, currentUser);

        mockMvc.perform(MockMvcRequestBuilders.post(SECTOR_USER_CONTROLLER_REGISTRATION_BASE_PATH + ADD_TO_SECTOR_ASSOCIATION_PATH + "/" + sectorAssociationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sectorUserInvitationDTO)))
                .andExpect(status().isNoContent());

    }

    @Test
    void inviteSectorUserToAccount_forbidden() throws Exception {
        AppUser currentUser = AppUser.builder().userId("user_id").roleType(SECTOR_USER).build();
        SectorUserInvitationDTO sectorUserInvitationDTO = buildMockSectorUserInvitationDTO();
        Long sectorAssociationId = 1L;

        when(appUserArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(appUserArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(currentUser, "inviteUserToSectorAssociation", sectorAssociationId.toString(), null, null);

        mockMvc.perform(MockMvcRequestBuilders.post(SECTOR_USER_CONTROLLER_REGISTRATION_BASE_PATH + ADD_TO_SECTOR_ASSOCIATION_PATH + "/" + sectorAssociationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sectorUserInvitationDTO)))
                .andExpect(status().isForbidden());

    }

    private SectorUserInvitationDTO buildMockSectorUserInvitationDTO() {
        return SectorUserInvitationDTO.builder()
                .email(SECTOR_USER_EMAIL)
                .firstName(SECTOR_USER_FNAME)
                .lastName(SECTOR_USER_LNAME)
                .roleCode(SECTOR_ROLE_CODE)
                .build();
    }
}
