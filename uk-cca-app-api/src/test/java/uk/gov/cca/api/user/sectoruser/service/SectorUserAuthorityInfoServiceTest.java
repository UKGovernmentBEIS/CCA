package uk.gov.cca.api.user.sectoruser.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.transform.NoticeRecipientMapper;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUsersAuthoritiesInfoDTO;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class SectorUserAuthorityInfoServiceTest {

    @InjectMocks
    private SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Mock
    private SectorAuthorityQueryService sectorAuthorityQueryService;

    @Mock
    private SectorUserInfoService sectorUserInfoService;

    @Mock
    private NoticeRecipientMapper noticeRecipientMapper;

    @Test
    void getSectorUsersAuthoritiesInfo() {
        Long sectorId = 1L;
        Long vbId = 1L;
        String userId = "userId";
        AppUser authUser = AppUser.builder()
                .userId("authUserId")
                .authorities(List.of(AppAuthority.builder().verificationBodyId(vbId).build()))
                .build();

        SectorUserAuthorityDTO sectorUserAuthorityDTO = SectorUserAuthorityDTO.builder()
        		.userId(userId)
        		.roleName("Administrator User")
        		.roleCode("roleCode")
        		.authorityStatus(ACTIVE)
        		.authorityCreationDate(LocalDateTime.now())
        		.contactType(ContactType.SECTOR_ASSOCIATION)
        		.build();

        SectorUserAuthoritiesDTO sectorUserAuthorities = SectorUserAuthoritiesDTO.builder()
                .authorities(List.of(sectorUserAuthorityDTO))
                .editable(true)
                .build();

        UserInfoDTO userInfo = UserInfoDTO.builder()
                .userId(userId)
                .firstName("FirstName")
                .lastName("Lastname")
                .build();

        SectorUserAuthorityInfoDTO expectedUserAuthInfo = SectorUserAuthorityInfoDTO.builder()
                .contactType("Sector Association")
                .roleCode("roleCode")
                .roleName("Administrator User")
                .firstName("FirstName")
                .lastName("Lastname")
                .userId(userId)
                .status(ACTIVE)
                .build();

        when(sectorAuthorityQueryService.getSectorUserAuthorities(authUser, sectorId)).thenReturn(sectorUserAuthorities);
        when(sectorUserInfoService.getSectorUsersInfo(List.of(userId))).thenReturn(List.of(userInfo));

        SectorUsersAuthoritiesInfoDTO sectorUsersAuthoritiesInfo = sectorUserAuthorityInfoService.getSectorUsersAuthoritiesInfo(authUser, sectorId);

        assertTrue(sectorUsersAuthoritiesInfo.isEditable());
        assertThat(sectorUsersAuthoritiesInfo.getAuthorities()).hasSize(1);
        assertEquals(expectedUserAuthInfo, sectorUsersAuthoritiesInfo.getAuthorities().get(0));

        verify(sectorAuthorityQueryService, times(1)).getSectorUserAuthorities(authUser, sectorId);
        verify(sectorUserInfoService, times(1)).getSectorUsersInfo(List.of(userId));
    }

    @Test
    void getSectorUsersUsersInfo() {
        Long sectorId = 1L;
        Long vbId = 1L;
        String userId = "userId";
        AppUser authUser = AppUser.builder()
                .userId("authUserId")
                .authorities(List.of(AppAuthority.builder().verificationBodyId(vbId).build()))
                .build();

        SectorUserAuthorityDTO sectorUserAuthorityDTO = SectorUserAuthorityDTO.builder()
                .userId(userId)
                .roleName("Administrator User")
                .roleCode("roleCode")
                .authorityStatus(ACTIVE)
                .authorityCreationDate(LocalDateTime.now())
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .build();

        SectorUserAuthoritiesDTO sectorUserAuthorities = SectorUserAuthoritiesDTO.builder()
                .authorities(List.of(sectorUserAuthorityDTO))
                .editable(true)
                .build();

        UserInfoDTO userInfoDTO = UserInfoDTO.builder().userId("user_1").firstName("fn").lastName("ln").email("email").build();

        AdditionalNoticeRecipientDTO noticeRecipientDTO = AdditionalNoticeRecipientDTO.builder()
        		.userId(userId)
                .firstName("fn")
                .lastName("ln")
                .email("email")
                .type(NoticeRecipientType.SECTOR_USER)
                .build();

        when(sectorAuthorityQueryService.getSectorUserAuthorities(authUser, sectorId)).thenReturn(sectorUserAuthorities);
        when(sectorUserInfoService.getSectorUsersInfo(List.of(userId))).thenReturn(List.of(userInfoDTO));
        when(noticeRecipientMapper.toSectorUSerNoticeRecipientDTO(userInfoDTO)).thenReturn(noticeRecipientDTO);

        // Invoke
        List<AdditionalNoticeRecipientDTO> sectorUsersUsersInfo = sectorUserAuthorityInfoService.getCandidateSectorUsersNoticeRecipients(authUser, sectorId);

        // Verify
        assertThat(noticeRecipientDTO).isEqualTo(sectorUsersUsersInfo.getFirst());
        verify(sectorAuthorityQueryService, times(1)).getSectorUserAuthorities(authUser, sectorId);
        verify(sectorUserInfoService, times(1)).getSectorUsersInfo(List.of(userId));
        verify(noticeRecipientMapper, times(1)).toSectorUSerNoticeRecipientDTO(userInfoDTO);
    }
}
