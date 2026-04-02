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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;


    @Test
    void validate() {
        final long accountId = 1L;
        final long sectorId = 2L;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final String signatory = "regulator";
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
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

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId))
                .thenReturn(sectorUserAuthorities);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification.getDecisionNotification(), appUser))
                .thenReturn(true);

        // Invoke
        List<BusinessViolation> results = validator.validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(results).isEmpty();
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(sectorAuthorityQueryService, times(1))
                .getSectorUserAuthorities(appUser, sectorId);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification.getDecisionNotification(), appUser);
    }

    @Test
    void validate_not_valid_users() {
        final long accountId = 1L;
        final long sectorId = 2L;
        final AppUser appUser = AppUser.builder().userId("user").build();
        final String signatory = "regulator";
        final Request request = Request.builder().build();
        addResourcesToRequest(accountId, request);
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
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

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId))
                .thenReturn(sectorUserAuthorities);
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification.getDecisionNotification(), appUser))
                .thenReturn(false);

        // Invoke
        List<BusinessViolation> results = validator.validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(results.isEmpty()).isFalse();
        assertThat(results).hasSize(2);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(sectorAuthorityQueryService, times(1))
                .getSectorUserAuthorities(appUser, sectorId);
        verify(decisionNotificationUsersValidator, times(1))
                .areUsersValid(requestTask, decisionNotification.getDecisionNotification(), appUser);
    }

    private void addResourcesToRequest(Long accountId, Request request) {
        RequestResource accountResource = RequestResource.builder()
                .resourceType(ResourceType.ACCOUNT)
                .resourceId(accountId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(accountResource);
    }
}
