package uk.gov.cca.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CcaRequestActionUserInfoResolver {

    private final UserAuthService userAuthService;
    private final OperatorAuthorityQueryService operatorAuthorityQueryService;
    private final SectorAuthorityQueryService sectorAuthorityQueryService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;

    @Transactional(readOnly = true)
    public String getUserFullName(final String userId) {
        return userAuthService.getUserByUserId(userId).getFullName();
    }

    @Transactional(readOnly = true)
    public Map<String, RequestActionUserInfo> getUsersInfo(final CcaDecisionNotification ccaDecisionNotification, final Request request) {
        final Set<String> userIds = Stream.of(
                        ccaDecisionNotification.getDecisionNotification().getOperators(),
                        ccaDecisionNotification.getSectorUsers(),
                        Set.of(ccaDecisionNotification.getDecisionNotification().getSignatory())
                ).flatMap(Collection::stream).collect(Collectors.toSet());

        return getUsersInfo(userIds, request.getAccountId());
    }

    private Map<String, RequestActionUserInfo> getUsersInfo(final Set<String> userIds,
                                                            final Long accountId) {
        Long sectorId = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId).getSectorAssociationId();

        // Get operators and sector users
        final List<AuthorityRoleDTO> authorities = operatorAuthorityQueryService.findOperatorUserAuthoritiesListByAccount(accountId);
        authorities.addAll(sectorAuthorityQueryService.findSectorUserAuthoritiesListBySectorAssociationId(sectorId));

        return userIds.stream()
                .collect(Collectors.toMap(id -> id, id -> this.createUserInfo(id, authorities)));
    }

    private RequestActionUserInfo createUserInfo(final String userId, final List<AuthorityRoleDTO> authorities) {
        final String role = authorities
                .stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst()
                .map(AuthorityRoleDTO::getRoleCode)
                .orElse(null);

        return RequestActionUserInfo.builder()
                .name(getUserFullName(userId))
                .roleCode(role)
                .build();
    }
}
