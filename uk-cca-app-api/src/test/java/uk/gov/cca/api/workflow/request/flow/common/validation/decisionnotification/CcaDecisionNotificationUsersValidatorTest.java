package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.netz.api.account.domain.dto.CaExternalContactsDTO;
import uk.gov.netz.api.account.service.CaExternalContactService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentValidationService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaDecisionNotificationUsersValidatorTest {

    @InjectMocks
    private CcaDecisionNotificationUsersValidator validator;

    @Mock
    private SectorAuthorityQueryService sectorAuthorityQueryService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Mock
    private CaExternalContactService caExternalContactService;

    @Mock
    private RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;

    @Test
    void validate() {
        final long accountId = 1L;
        final long sectorId = 2L;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final String signatory = "regulator";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(accountId).build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1"))
                .decisionNotification(DecisionNotification.builder()
                        .operators(Set.of("operator1"))
                        .externalContacts(Set.of(1L))
                        .signatory(signatory)
                        .build())
                .build();

        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();
        final SectorUserAuthoritiesDTO sectorUserAuthorities = SectorUserAuthoritiesDTO.builder()
                .authorities(List.of(
                        SectorUserAuthorityDTO.builder().userId("sector1").authorityStatus(AuthorityStatus.ACTIVE).build(),
                        SectorUserAuthorityDTO.builder().userId("sector2").authorityStatus(AuthorityStatus.ACTIVE).build()
                ))
                .build();
        final UserAuthoritiesDTO operatorAuthorities = UserAuthoritiesDTO.builder()
                .authorities(List.of(
                        UserAuthorityDTO.builder().userId("operator1").authorityStatus(AuthorityStatus.ACTIVE).build(),
                        UserAuthorityDTO.builder().userId("operator2").authorityStatus(AuthorityStatus.ACTIVE).build()
                ))
                .build();
        final CaExternalContactsDTO externalContacts = CaExternalContactsDTO.builder()
                .caExternalContacts(List.of(
                        CaExternalContactDTO.builder().id(1L).build(),
                        CaExternalContactDTO.builder().id(2L).build()
                ))
                .build();

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId))
                .thenReturn(sectorUserAuthorities);
        when(operatorAuthorityQueryService.getAccountAuthorities(appUser, accountId))
                .thenReturn(operatorAuthorities);
        when(caExternalContactService.getCaExternalContacts(appUser))
                .thenReturn(externalContacts);
        when(requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(requestTask, signatory))
                .thenReturn(true);

        // Invoke
        List<BusinessViolation> results = validator.validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(results).isEmpty();
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(sectorAuthorityQueryService, times(1))
                .getSectorUserAuthorities(appUser, sectorId);
        verify(operatorAuthorityQueryService, times(1))
                .getAccountAuthorities(appUser, accountId);
        verify(caExternalContactService, times(1))
                .getCaExternalContacts(appUser);
        verify(requestTaskAssignmentValidationService, times(1))
                .hasUserPermissionsToBeAssignedToTask(requestTask, signatory);
    }

    @Test
    void validate_not_valid_users() {
        final long accountId = 1L;
        final long sectorId = 2L;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final String signatory = "regulator";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(accountId).build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector3"))
                .decisionNotification(DecisionNotification.builder()
                        .operators(Set.of("operator3"))
                        .externalContacts(Set.of(3L))
                        .signatory(signatory)
                        .build())
                .build();

        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();
        final SectorUserAuthoritiesDTO sectorUserAuthorities = SectorUserAuthoritiesDTO.builder()
                .authorities(List.of(
                        SectorUserAuthorityDTO.builder().userId("sector1").authorityStatus(AuthorityStatus.ACTIVE).build(),
                        SectorUserAuthorityDTO.builder().userId("sector2").authorityStatus(AuthorityStatus.ACTIVE).build()
                ))
                .build();
        final UserAuthoritiesDTO operatorAuthorities = UserAuthoritiesDTO.builder()
                .authorities(List.of(
                        UserAuthorityDTO.builder().userId("operator1").authorityStatus(AuthorityStatus.ACTIVE).build(),
                        UserAuthorityDTO.builder().userId("operator2").authorityStatus(AuthorityStatus.ACTIVE).build()
                ))
                .build();
        final CaExternalContactsDTO externalContacts = CaExternalContactsDTO.builder()
                .caExternalContacts(List.of(
                        CaExternalContactDTO.builder().id(1L).build(),
                        CaExternalContactDTO.builder().id(2L).build()
                ))
                .build();

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId))
                .thenReturn(sectorUserAuthorities);
        when(operatorAuthorityQueryService.getAccountAuthorities(appUser, accountId))
                .thenReturn(operatorAuthorities);
        when(caExternalContactService.getCaExternalContacts(appUser))
                .thenReturn(externalContacts);
        when(requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(requestTask, signatory))
                .thenReturn(true);

        // Invoke
        List<BusinessViolation> results = validator.validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(results).hasSize(3);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(sectorAuthorityQueryService, times(1))
                .getSectorUserAuthorities(appUser, sectorId);
        verify(operatorAuthorityQueryService, times(1))
                .getAccountAuthorities(appUser, accountId);
        verify(caExternalContactService, times(1))
                .getCaExternalContacts(appUser);
        verify(requestTaskAssignmentValidationService, times(1))
                .hasUserPermissionsToBeAssignedToTask(requestTask, signatory);
    }

    @Test
    void validate_no_permission_for_signatory() {
        final long accountId = 1L;
        final long sectorId = 2L;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final String signatory = "regulator";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(accountId).build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1"))
                .decisionNotification(DecisionNotification.builder()
                        .operators(Set.of("operator1"))
                        .externalContacts(Set.of(1L))
                        .signatory(signatory)
                        .build())
                .build();

        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();
        final SectorUserAuthoritiesDTO sectorUserAuthorities = SectorUserAuthoritiesDTO.builder()
                .authorities(List.of(
                        SectorUserAuthorityDTO.builder().userId("sector1").authorityStatus(AuthorityStatus.ACTIVE).build(),
                        SectorUserAuthorityDTO.builder().userId("sector2").authorityStatus(AuthorityStatus.ACTIVE).build()
                ))
                .build();
        final UserAuthoritiesDTO operatorAuthorities = UserAuthoritiesDTO.builder()
                .authorities(List.of(
                        UserAuthorityDTO.builder().userId("operator1").authorityStatus(AuthorityStatus.ACTIVE).build(),
                        UserAuthorityDTO.builder().userId("operator2").authorityStatus(AuthorityStatus.ACTIVE).build()
                ))
                .build();
        final CaExternalContactsDTO externalContacts = CaExternalContactsDTO.builder()
                .caExternalContacts(List.of(
                        CaExternalContactDTO.builder().id(1L).build(),
                        CaExternalContactDTO.builder().id(2L).build()
                ))
                .build();

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId))
                .thenReturn(sectorUserAuthorities);
        when(operatorAuthorityQueryService.getAccountAuthorities(appUser, accountId))
                .thenReturn(operatorAuthorities);
        when(caExternalContactService.getCaExternalContacts(appUser))
                .thenReturn(externalContacts);
        when(requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(requestTask, signatory))
                .thenReturn(false);

        // Invoke
        List<BusinessViolation> results = validator.validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(results).hasSize(1);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(sectorAuthorityQueryService, times(1))
                .getSectorUserAuthorities(appUser, sectorId);
        verify(operatorAuthorityQueryService, times(1))
                .getAccountAuthorities(appUser, accountId);
        verify(caExternalContactService, times(1))
                .getCaExternalContacts(appUser);
        verify(requestTaskAssignmentValidationService, times(1))
                .hasUserPermissionsToBeAssignedToTask(requestTask, signatory);
    }

    @Test
    void validate_empty_users_valid() {
        final long accountId = 1L;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(accountId).build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of())
                .decisionNotification(DecisionNotification.builder()
                        .operators(Set.of())
                        .externalContacts(Set.of())
                        .build())
                .build();

        // Invoke
        List<BusinessViolation> results = validator.validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(results).isEmpty();
        verifyNoInteractions(accountReferenceDetailsService, sectorAuthorityQueryService,
                operatorAuthorityQueryService, caExternalContactService, requestTaskAssignmentValidationService);
    }

    @Test
    void validate_no_users_valid() {
        final long accountId = 1L;
        final long sectorId = 2L;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final String signatory = "regulator";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(accountId).build())
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1"))
                .decisionNotification(DecisionNotification.builder()
                        .operators(Set.of("operator1"))
                        .externalContacts(Set.of(1L))
                        .signatory(signatory)
                        .build())
                .build();

        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();
        final SectorUserAuthoritiesDTO sectorUserAuthorities = SectorUserAuthoritiesDTO.builder()
                .authorities(List.of())
                .build();
        final UserAuthoritiesDTO operatorAuthorities = UserAuthoritiesDTO.builder()
                .authorities(List.of())
                .build();
        final CaExternalContactsDTO externalContacts = CaExternalContactsDTO.builder()
                .caExternalContacts(List.of())
                .build();

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId))
                .thenReturn(sectorUserAuthorities);
        when(operatorAuthorityQueryService.getAccountAuthorities(appUser, accountId))
                .thenReturn(operatorAuthorities);
        when(caExternalContactService.getCaExternalContacts(appUser))
                .thenReturn(externalContacts);
        when(requestTaskAssignmentValidationService.hasUserPermissionsToBeAssignedToTask(requestTask, signatory))
                .thenReturn(true);

        // Invoke
        List<BusinessViolation> results = validator.validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(results).hasSize(3);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(sectorAuthorityQueryService, times(1))
                .getSectorUserAuthorities(appUser, sectorId);
        verify(operatorAuthorityQueryService, times(1))
                .getAccountAuthorities(appUser, accountId);
        verify(caExternalContactService, times(1))
                .getCaExternalContacts(appUser);
        verify(requestTaskAssignmentValidationService, times(1))
                .hasUserPermissionsToBeAssignedToTask(requestTask, signatory);
    }
}
