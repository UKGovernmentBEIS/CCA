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
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountHistoryDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountInfoDTO;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaFileEvidenceTokenService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesMoaUpdateService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.security.AuthorizedRoleAspect;
import uk.gov.netz.api.token.FileToken;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaReceivedAmountControllerTest {
    private static final String CONTROLLER_PATH = "/v1.0/subsistence-fees/moas/%s/received-amount";

    private MockMvc mockMvc;

    @InjectMocks
    private SubsistenceFeesMoaReceivedAmountController subsistenceFeesMoaReceivedAmountController;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private SubsistenceFeesMoaFileEvidenceTokenService fileEvidenceTokenService;

    @Mock
    private SubsistenceFeesMoaUpdateService subsistenceFeesMoaUpdateService;

    @Mock
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(subsistenceFeesMoaReceivedAmountController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        subsistenceFeesMoaReceivedAmountController = (SubsistenceFeesMoaReceivedAmountController) aopProxy.getProxy();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(subsistenceFeesMoaReceivedAmountController)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .build();
    }

    @Test
    void generateGetMoaReceivedAmountEvidenceFileToken() throws Exception {
        Long moaId = 1L;
        UUID evidenceFileUuid = UUID.randomUUID();
        FileToken token = FileToken.builder().token(evidenceFileUuid.toString()).build();
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(submitter);
        when(fileEvidenceTokenService.generateGetFileEvidenceToken(moaId, evidenceFileUuid, submitter)).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format(CONTROLLER_PATH, moaId) + "/evidence")
                        .param("fileEvidenceUuid", String.valueOf(evidenceFileUuid))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(fileEvidenceTokenService, times(1)).generateGetFileEvidenceToken(moaId, evidenceFileUuid, submitter);
    }

    @Test
    void updateSubsistenceFeesMoaReceivedAmount() throws Exception {
        Long moaId = 1L;
        BigDecimal transactionAmount = BigDecimal.valueOf(185);
        AppUser submitter = AppUser.builder()
                .roleType(REGULATOR)
                .firstName("FirstName")
                .lastName("LastName")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();
        SubsistenceFeesMoaReceivedAmountDetailsDTO detailsDto = SubsistenceFeesMoaReceivedAmountDetailsDTO.builder()
                .transactionAmount(transactionAmount)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(submitter);

        mockMvc.perform(MockMvcRequestBuilders.post(String.format(CONTROLLER_PATH, moaId))
                        .content(mapper.writeValueAsString(detailsDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(subsistenceFeesMoaUpdateService, times(1)).updateSubsistenceFeesMoaReceivedAmount(moaId, detailsDto, submitter);
    }

    @Test
    void getSubsistenceFeesMoaReceivedAmountInfo() throws Exception {
        Long moaId = 1L;
        LocalDateTime submissionDate = LocalDateTime.of(2020, 1, 1, 1, 1);
        BigDecimal transactionAmount = BigDecimal.valueOf(185);
        String submitter = "submitter";
        String comments = "bla bla bla";
        UUID fileEvidenceUuid1 = UUID.randomUUID();
        String evidenceFileName1 = "EvidenceFile1";
        Map<UUID, String> evidenceFiles = Map.of(fileEvidenceUuid1, evidenceFileName1);

        SubsistenceFeesMoaReceivedAmountHistoryDTO moaReceivedAmountHistoryDTO = SubsistenceFeesMoaReceivedAmountHistoryDTO.builder()
                .transactionAmount(transactionAmount)
                .submitter(submitter)
                .transactionAmount(transactionAmount)
                .submissionDate(submissionDate)
                .comments(comments)
                .evidenceFiles(evidenceFiles)
                .build();

        SubsistenceFeesMoaReceivedAmountInfoDTO detailsDTO = SubsistenceFeesMoaReceivedAmountInfoDTO.builder()
                .receivedAmount(BigDecimal.valueOf(5000))
                .currentTotalAmount(BigDecimal.valueOf(7000))
                .receivedAmountHistoryList(List.of(moaReceivedAmountHistoryDTO))
                .build();


        when(subsistenceFeesMoaQueryService.getSubsistenceFeesMoaReceivedAmountInfo(moaId)).thenReturn(detailsDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(String.format(CONTROLLER_PATH, moaId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(subsistenceFeesMoaQueryService, times(1)).getSubsistenceFeesMoaReceivedAmountInfo(moaId);
    }
}
