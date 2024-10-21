package uk.gov.cca.api.user.operator.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.transform.NoticeRecipientMapper;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityQueryService;
import uk.gov.cca.api.user.operator.domain.OperatorAuthoritiesInfoDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.user.operator.service.OperatorUserInfoService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserAuthorityInfoServiceTest {

    @InjectMocks
    private OperatorUserAuthorityInfoService service;

    @Mock
    private CcaOperatorAuthorityQueryService operatorAuthorityQueryService;

    @Mock
    private OperatorUserInfoService operatorUserInfoService;

    @Mock
    private NoticeRecipientMapper noticeRecipientMapper;

    @Test
    void getOperatorAuthoritiesInfo() {
        Long accountId = 1L;
        String roleCode = "operator_basic_user";
        String roleName = "Operator";
        String userId = "userId";

        AppUser appUser = AppUser.builder().userId(userId).roleType(RoleTypeConstants.REGULATOR).build();

        final OperatorAuthorityDTO operatorAuthorityDTO_1 = OperatorAuthorityDTO.builder()
        		.userId("user_1")
        		.roleName(roleName)
        		.roleCode(roleCode)
        		.authorityStatus(AuthorityStatus.PENDING)
        		.authorityCreationDate(null)
        		.contactType(ContactType.OPERATOR)
        		.build();

        final OperatorAuthorityDTO operatorAuthorityDTO_2 = OperatorAuthorityDTO.builder()
        		.userId("user_2")
        		.roleName(roleName)
        		.roleCode(roleCode)
        		.authorityStatus(AuthorityStatus.ACTIVE)
        		.authorityCreationDate(null)
        		.contactType(ContactType.OPERATOR)
        		.build();

        final OperatorAuthoritiesDTO operatorAuthoritiesDTO = OperatorAuthoritiesDTO.builder()
                .authorities(List.of(operatorAuthorityDTO_1, operatorAuthorityDTO_2))
                .editable(true)
                .build();

        List<String> userIds = operatorAuthoritiesDTO.getAuthorities().stream().map(UserAuthorityDTO::getUserId).toList();

        final List<UserInfoDTO> userInfoDTOS = List.of(
                UserInfoDTO.builder().userId("user_1").firstName("John").lastName("Rambo").build(),
                UserInfoDTO.builder().userId("user_2").firstName("Chuck").lastName("Norris").build());

        when(operatorAuthorityQueryService.getOperatorAuthorities(appUser, accountId)).thenReturn(operatorAuthoritiesDTO);
        when(operatorUserInfoService.getOperatorUsersInfo(userIds)).thenReturn(userInfoDTOS);

        //invoke
        final OperatorAuthoritiesInfoDTO operatorAuthoritiesInfo = service.getOperatorAuthoritiesInfo(appUser, accountId);

        //assert
        assertThat(operatorAuthoritiesInfo.getAuthorities().size()).isEqualTo(2);
        assertThat(operatorAuthoritiesInfo.isEditable()).isTrue();
        assertThat(operatorAuthoritiesInfo.getAuthorities().get(0).getFirstName()).isEqualTo("John");

    }

    @Test
    void getOperatorUsersInfo() {
        Long accountId = 1L;
        String roleCode = "operator_basic_user";
        String roleName = "Operator";
        String userId = "userId";

        AppUser appUser = AppUser.builder().userId(userId).roleType(RoleTypeConstants.REGULATOR).build();

        final OperatorAuthorityDTO operatorAuthorityDTO_1 = OperatorAuthorityDTO.builder()
                .userId("user_1")
                .roleName(roleName)
                .roleCode(roleCode)
                .authorityStatus(AuthorityStatus.ACTIVE)
                .authorityCreationDate(null)
                .contactType(ContactType.OPERATOR)
                .build();

        final OperatorAuthoritiesDTO operatorAuthoritiesDTO = OperatorAuthoritiesDTO.builder()
                .authorities(List.of(operatorAuthorityDTO_1))
                .editable(true)
                .build();

        List<String> userIds = operatorAuthoritiesDTO.getAuthorities().stream().map(UserAuthorityDTO::getUserId).toList();

        UserInfoDTO userInfoDTO = UserInfoDTO.builder().userId("user_1").firstName("fn").lastName("ln").email("email").build();
        final List<UserInfoDTO> userInfoDTOS = List.of(userInfoDTO);

        AdditionalNoticeRecipientDTO noticeRecipientDTO = AdditionalNoticeRecipientDTO.builder()
        		.userId(userId)
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.OPERATOR)
                .build();

        when(operatorAuthorityQueryService.getOperatorAuthorities(appUser, accountId)).thenReturn(operatorAuthoritiesDTO);
        when(operatorUserInfoService.getOperatorUsersInfo(userIds)).thenReturn(userInfoDTOS);
        when(noticeRecipientMapper.toOperatorNoticeRecipientDTO(userInfoDTO)).thenReturn(noticeRecipientDTO);

        // Invoke
        List<AdditionalNoticeRecipientDTO> operatorUsersInfo = service.getCandidateOperatorNoticeRecipients(appUser, accountId);

        // Verify
        assertThat(noticeRecipientDTO).isEqualTo(operatorUsersInfo.getFirst());
        verify(operatorAuthorityQueryService, times(1)).getOperatorAuthorities(appUser, accountId);
        verify(operatorUserInfoService, times(1)).getOperatorUsersInfo(userIds);
        verify(noticeRecipientMapper, times(1)).toOperatorNoticeRecipientDTO(userInfoDTO);
    }

}
