package uk.gov.cca.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaRequestActionUserInfoResolverTest {

    @InjectMocks
    private CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Mock
    private SectorAuthorityQueryService sectorAuthorityQueryService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Test
    void getUsersInfo() {
        final long accountId = 1L;
        final CcaDecisionNotification ccaDecisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1"))
                .decisionNotification(DecisionNotification.builder()
                        .externalContacts(Set.of(1L, 2L))
                        .operators(Set.of("operator1"))
                        .signatory("regulator")
                        .build())
                .build();
        final Request request = Request.builder().accountId(accountId).build();

        final long sectorId = 2L;
        final TargetUnitAccountDetailsDTO accountDetails = TargetUnitAccountDetailsDTO.builder()
                .sectorAssociationId(sectorId)
                .build();

        List<AuthorityRoleDTO> operators = new ArrayList<>();
        operators.add(AuthorityRoleDTO.builder().userId("operator1").roleCode("operator_user").build());
        operators.add(AuthorityRoleDTO.builder().userId("operator2").roleCode("operator_user").build());

        final List<AuthorityRoleDTO> sectors = List.of(
                AuthorityRoleDTO.builder().userId("sector1").roleCode("sector_user_administrator").build(),
                AuthorityRoleDTO.builder().userId("sector2").roleCode("sector_user_administrator").build()
        );

        final Map<String, RequestActionUserInfo> expected = Map.of(
                "operator1",
                RequestActionUserInfo.builder()
                        .name("Operator Last")
                        .contactTypes(Set.of())
                        .roleCode("operator_user")
                        .build(),
                "sector1",
                RequestActionUserInfo.builder()
                        .name("Sector Last")
                        .contactTypes(Set.of())
                        .roleCode("sector_user_administrator")
                        .build(),
                "regulator",
                RequestActionUserInfo.builder()
                        .name("Regulator Last")
                        .contactTypes(Set.of())
                        .build()
        );

        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId))
                .thenReturn(accountDetails);
        when(operatorAuthorityQueryService.findOperatorUserAuthoritiesListByAccount(accountId))
                .thenReturn(operators);
        when(sectorAuthorityQueryService.findSectorUserAuthoritiesListBySectorAssociationId(sectorId))
                .thenReturn(sectors);
        when(userAuthService.getUserByUserId("operator1"))
                .thenReturn(UserInfoDTO.builder().userId("operator1").firstName("Operator").lastName("Last").build());
        when(userAuthService.getUserByUserId("sector1"))
                .thenReturn(UserInfoDTO.builder().userId("sector1").firstName("Sector").lastName("Last").build());
        when(userAuthService.getUserByUserId("regulator"))
                .thenReturn(UserInfoDTO.builder().userId("regulator").firstName("Regulator").lastName("Last").build());

        // Invoke
        Map<String, RequestActionUserInfo> actual = ccaRequestActionUserInfoResolver
                .getUsersInfo(ccaDecisionNotification, request);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(accountReferenceDetailsService, times(1))
                .getTargetUnitAccountDetails(accountId);
        verify(operatorAuthorityQueryService, times(1))
                .findOperatorUserAuthoritiesListByAccount(accountId);
        verify(sectorAuthorityQueryService, times(1))
                .findSectorUserAuthoritiesListBySectorAssociationId(sectorId);
        verify(userAuthService, times(1)).getUserByUserId("operator1");
        verify(userAuthService, times(1)).getUserByUserId("sector1");
        verify(userAuthService, times(1)).getUserByUserId("regulator");
    }
}
