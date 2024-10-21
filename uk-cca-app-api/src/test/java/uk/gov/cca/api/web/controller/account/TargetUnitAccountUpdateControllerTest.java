package uk.gov.cca.api.web.controller.account;

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
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountResponsiblePersonDTO;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountSicCodeDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.web.config.AppUserArgumentResolver;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.security.AppSecurityComponent;
import uk.gov.netz.api.security.AuthorizationAspectUserResolver;
import uk.gov.netz.api.security.AuthorizedAspect;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@EnableWebMvc
class TargetUnitAccountUpdateControllerTest {

    private static final String BASE_PATH = "/v1.0/target-unit-accounts/1/update/";

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @InjectMocks
    private TargetUnitAccountUpdateController controller;

    @Mock
    private TargetUnitAccountUpdateService targetUnitAccountUpdateService;

    @Mock
    private AppSecurityComponent appSecurityComponent;

    @Mock
    private AppUserAuthorizationService appUserAuthorizationService;

    @BeforeEach
    public void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(appSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(appUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);
        controller = (TargetUnitAccountUpdateController) aopProxy.getProxy();

        mapper = new ObjectMapper();

        FormattingConversionService conversionService = new FormattingConversionService();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AppUserArgumentResolver(appSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .setConversionService(conversionService)
                .setValidator(Mockito.mock(Validator.class))
                .build();
    }

    @Test
    void updateTargetUnitAccountSicCode() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final UpdateTargetUnitAccountSicCodeDTO sicCodeDTO = UpdateTargetUnitAccountSicCodeDTO.builder()
                .sicCode("111")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "sic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sicCodeDTO)))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountUpdateService, times(1))
                .updateTargetUnitAccountSicCode(accountId, sicCodeDTO);
    }

    @Test
    void updateTargetUnitAccountSicCode_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final UpdateTargetUnitAccountSicCodeDTO sicCodeDTO = UpdateTargetUnitAccountSicCodeDTO.builder()
                .sicCode("111")
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateTargetUnitAccountSicCode", Long.toString(accountId));

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "sic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sicCodeDTO)))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(targetUnitAccountUpdateService);
    }

    @Test
    void updateTargetUnitAccountFinancialIndependenceStatusCode() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO financialCodeDTO = UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO.builder()
                .financialIndependenceStatus(FinancialIndependenceStatus.FINANCIALLY_INDEPENDENT)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "financial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(financialCodeDTO)))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountUpdateService, times(1))
                .updateTargetUnitAccountFinancialIndependenceStatusCode(accountId, financialCodeDTO);
    }

    @Test
    void updateTargetUnitAccountFinancialIndependenceStatusCode_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO financialCodeDTO = UpdateTargetUnitAccountFinancialIndependenceStatusCodeDTO.builder()
                .financialIndependenceStatus(FinancialIndependenceStatus.FINANCIALLY_INDEPENDENT)
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateTargetUnitAccountFinancialIndependenceStatusCode", Long.toString(accountId));

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "financial")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(financialCodeDTO)))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(targetUnitAccountUpdateService);
    }

    @Test
    void updateTargetUnitAccountResponsiblePerson() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final UpdateTargetUnitAccountResponsiblePersonDTO responsiblePersonDTO = UpdateTargetUnitAccountResponsiblePersonDTO.builder()
                .jobTitle("Job")
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("30")
                        .number("9999999999")
                        .build())
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "responsible")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(responsiblePersonDTO)))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountUpdateService, times(1))
                .updateTargetUnitAccountResponsiblePerson(accountId, responsiblePersonDTO);
    }

    @Test
    void updateTargetUnitAccountResponsiblePerson_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final UpdateTargetUnitAccountResponsiblePersonDTO responsiblePersonDTO = UpdateTargetUnitAccountResponsiblePersonDTO.builder()
                .jobTitle("Job")
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("30")
                        .number("9999999999")
                        .build())
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateTargetUnitAccountResponsiblePerson", Long.toString(accountId));

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "responsible")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(responsiblePersonDTO)))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(targetUnitAccountUpdateService);
    }

    @Test
    void updateTargetUnitAccountAdministrativePerson() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final TargetUnitAccountContactDTO accountContactDTO = TargetUnitAccountContactDTO.builder()
                .email("xx@test.gr")
                .firstName("First")
                .lastName("Last")
                .jobTitle("Job")
                .address(AccountAddressDTO.builder()
                        .line1("Line 1")
                        .line2("Line 2")
                        .city("City")
                        .county("County")
                        .postcode("code")
                        .country("Country")
                        .build())
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("30")
                        .number("9999999999")
                        .build())
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "administrative")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountContactDTO)))
                .andExpect(status().isNoContent());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verify(targetUnitAccountUpdateService, times(1))
                .updateTargetUnitAccountAdministrativePerson(accountId, accountContactDTO);
    }

    @Test
    void updateTargetUnitAccountAdministrativePerson_forbidden() throws Exception {
        final AppUser user = AppUser.builder().roleType(RoleTypeConstants.REGULATOR).build();
        final long accountId = 1L;
        final TargetUnitAccountContactDTO accountContactDTO = TargetUnitAccountContactDTO.builder()
                .email("xx@test.gr")
                .firstName("First")
                .lastName("Last")
                .jobTitle("Job")
                .address(AccountAddressDTO.builder()
                        .line1("Line 1")
                        .line2("Line 2")
                        .city("City")
                        .county("County")
                        .postcode("code")
                        .country("Country")
                        .build())
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("30")
                        .number("9999999999")
                        .build())
                .build();

        when(appSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(appUserAuthorizationService)
                .authorize(user, "updateTargetUnitAccountAdministrativePerson", Long.toString(accountId));

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + "administrative")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(accountContactDTO)))
                .andExpect(status().isForbidden());

        verify(appSecurityComponent, times(1)).getAuthenticatedUser();
        verifyNoInteractions(targetUnitAccountUpdateService);
    }
}
