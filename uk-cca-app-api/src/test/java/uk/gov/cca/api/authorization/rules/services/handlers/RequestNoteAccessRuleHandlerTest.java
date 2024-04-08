package uk.gov.cca.api.authorization.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.cca.api.authorization.rules.services.authorityinfo.providers.RequestNoteAuthorityInfoProvider;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.cca.api.authorization.rules.services.handlers.RequestNoteAccessRuleHandler;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestNoteAccessRuleHandlerTest {

    @InjectMocks
    private RequestNoteAccessRuleHandler requestNoteAccessRuleHandler;

    @Mock
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private RequestNoteAuthorityInfoProvider requestNoteAuthorityInfoProvider;

    @Test
    void evaluateRules() {

        final long noteId = 2;
        final long accountId = 1;
        final long verificationBodyId = 3;
        final AppUser appUser = AppUser.builder().
            roleType(RoleType.REGULATOR)
            .build();
        final AuthorizationRuleScopePermission authorizationRule = AuthorizationRuleScopePermission.builder().build();
        final Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule);
        final AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            .accountId(accountId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .verificationBodyId(verificationBodyId)
            .build();
        final RequestAuthorityInfoDTO requestAuthorityInfoDTO = RequestAuthorityInfoDTO.builder()
            .authorityInfo(ResourceAuthorityInfo.builder()
                .accountId(accountId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .verificationBodyId(verificationBodyId)
                .build())
            .build();

        when(requestNoteAuthorityInfoProvider.getRequestNoteInfo(noteId)).thenReturn(requestAuthorityInfoDTO);

        requestNoteAccessRuleHandler.evaluateRules(rules, appUser, String.valueOf(noteId));

        verify(requestNoteAuthorityInfoProvider, times(1)).getRequestNoteInfo(noteId);
        verify(appAuthorizationService, times(1)).authorize(appUser, authorizationCriteria);
    }

    @Test
    void evaluateRules_resource_forbidden() {

        final long noteId = 2;
        final long accountId = 1;
        final long verificationBodyId = 3;
        final AppUser appUser = AppUser.builder().
            roleType(RoleType.OPERATOR)
            .build();
        final AuthorizationRuleScopePermission authorizationRule = AuthorizationRuleScopePermission.builder().build();
        final Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule);
        final AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            .accountId(accountId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .verificationBodyId(verificationBodyId)
            .build();
        final RequestAuthorityInfoDTO requestAuthorityInfoDTO = RequestAuthorityInfoDTO.builder()
            .authorityInfo(ResourceAuthorityInfo.builder()
                .accountId(accountId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .verificationBodyId(verificationBodyId)
                .build())
            .build();

        when(requestNoteAuthorityInfoProvider.getRequestNoteInfo(noteId)).thenReturn(requestAuthorityInfoDTO);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(appAuthorizationService)
            .authorize(appUser, authorizationCriteria);

        final BusinessException be = assertThrows(BusinessException.class, () ->
            requestNoteAccessRuleHandler.evaluateRules(rules, appUser, String.valueOf(noteId)));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);

        verify(requestNoteAuthorityInfoProvider, times(1)).getRequestNoteInfo(noteId);
        verify(appAuthorizationService, times(1)).authorize(appUser, authorizationCriteria);
    }
}
