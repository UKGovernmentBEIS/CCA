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
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaFacilityMarkingStatusDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaTargetUnitSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesSearchCriteria;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaDocumentService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFacilityMarkingStatusService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;
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
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.token.FileToken;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
class SubsistenceFeesMoaControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/subsistence-fees/moas/";

    private MockMvc mockMvc;

    @InjectMocks
    private SubsistenceFeesMoaController subsistenceFeesMoaController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    @Mock
    private SubsistenceFeesMoaTargetUnitQueryService subsistenceFeesMoaTargetUnitQueryService;

    @Mock
    private SubsistenceFeesMoaDocumentService subsistenceFeesMoaDocumentService;

    @Mock
    private SubsistenceFeesMoaFacilityMarkingStatusService markingFacilitiesStatusService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(subsistenceFeesMoaController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        subsistenceFeesMoaController = (SubsistenceFeesMoaController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(subsistenceFeesMoaController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void getSubsistenceFeesMoaDetailsById() throws Exception {
        Long moaId = 1L;
        LocalDateTime date = LocalDateTime.now();
        FileInfoDTO fileInfoDTO = FileInfoDTO.builder().build();

        SubsistenceFeesMoaDetailsDTO sfrMoaDetailsDTO = new SubsistenceFeesMoaDetailsDTO(1L, "CCACM1200", "ADS", "name",
                fileInfoDTO, date, PaymentStatus.AWAITING_PAYMENT, 10L, 10L,
                BigDecimal.valueOf(1000L), BigDecimal.valueOf(1000L), BigDecimal.valueOf(1000L), BigDecimal.valueOf(185L), 1L);
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(subsistenceFeesMoaQueryService.getSubsistenceFeesMoaDetailsById(moaId)).thenReturn(sfrMoaDetailsDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + moaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("CCACM1200"));

        verify(subsistenceFeesMoaQueryService, times(1)).getSubsistenceFeesMoaDetailsById(moaId);
    }

    @Test
    void getSubsistenceFeesMoaDetailsById_forbidden() throws Exception {
        Long moaId = 1L;
        AppUser appUser = AppUser.builder()
                .userId("userId")
                .roleType(SECTOR_USER)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "getSubsistenceFeesMoaDetailsById", Long.toString(moaId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + moaId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(subsistenceFeesMoaQueryService);
    }

    @Test
    void generateGetSubsistenceFeesMoaDocumentToken() throws Exception {
        Long moaId = 1L;
        UUID documentUuid = UUID.randomUUID();
        FileToken token = FileToken.builder().token(documentUuid.toString()).build();
        final AppUser user = AppUser.builder()
                .roleType(RoleTypeConstants.REGULATOR)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(subsistenceFeesMoaDocumentService.generateGetFileDocumentToken(moaId, documentUuid)).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + moaId + "/document")
                        .param("fileDocumentUuid", String.valueOf(documentUuid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(subsistenceFeesMoaDocumentService, times(1)).generateGetFileDocumentToken(moaId, documentUuid);
    }

    @Test
    void generateGetSubsistenceFeesMoaDocumentToken_forbidden() throws Exception {
        Long moaId = 1L;
        UUID documentUuid = UUID.randomUUID();
        AppUser appUser = AppUser.builder()
                .userId("userId")
                .roleType(SECTOR_USER)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(appUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(appUser, "generateGetSubsistenceFeesMoaDocumentToken", Long.toString(moaId), null, null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(CONTROLLER_PATH + moaId + "/document")
                        .param("fileDocumentUuid", String.valueOf(documentUuid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(subsistenceFeesMoaDocumentService);
    }

    @Test
    void getSubsistenceFeesMoaTargetUnits() throws Exception {
        final AppUser user = AppUser.builder().roleType(REGULATOR).build();
        final int page = 0;
        final int pageSize = 30;
        final long moaId = 1L;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
                .paging(pagingRequest)
                .build();
        SubsistenceFeesMoaTargetUnitSearchResultInfoDTO dto =
                new SubsistenceFeesMoaTargetUnitSearchResultInfoDTO(1L, "ADS-001", null, null, null);
        SubsistenceFeesMoaTargetUnitSearchResults results = SubsistenceFeesMoaTargetUnitSearchResults.builder()
                .subsistenceFeesMoaTargetUnits(List.of(dto))
                .total(1L)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(subsistenceFeesMoaTargetUnitQueryService.getSubsistenceFeesMoaTargetUnits(moaId, criteria)).thenReturn(results);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + moaId + "/target-units")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(results.getTotal()))
                .andExpect(jsonPath("$.subsistenceFeesMoaTargetUnits[0].moaTargetUnitId").value(1L))
                .andExpect(jsonPath("$.subsistenceFeesMoaTargetUnits[0].businessId").value("ADS-001"));

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(subsistenceFeesMoaTargetUnitQueryService, times(1)).getSubsistenceFeesMoaTargetUnits(moaId, criteria);
    }

    @Test
    void getSubsistenceFeesMoaTargetUnits_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(SECTOR_USER).userId("userId").build();
        final int page = 0;
        final int pageSize = 30;
        final long moaId = 1L;
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesSearchCriteria criteria = SubsistenceFeesSearchCriteria.builder()
                .paging(pagingRequest)
                .build();

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "getSubsistenceFeesMoaTargetUnits", String.valueOf(1L), null, null);

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + moaId + "/target-units")
                        .content(mapper.writeValueAsString(criteria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(subsistenceFeesMoaTargetUnitQueryService, never()).getSubsistenceFeesMoaTargetUnits(moaId, criteria);
    }

    @Test
    void markFacilitiesStatusByMoaId() throws Exception {
        Long moaId = 1L;
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

        mockMvc.perform(MockMvcRequestBuilders.post(CONTROLLER_PATH + moaId + "/facilities/mark")
                        .content(mapper.writeValueAsString(statusDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(markingFacilitiesStatusService, times(1)).markMoaFacilitiesStatusByMoaId(submitter, moaId, statusDTO);
    }
}
