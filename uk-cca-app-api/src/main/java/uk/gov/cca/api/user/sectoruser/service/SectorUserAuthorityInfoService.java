package uk.gov.cca.api.user.sectoruser.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.transform.NoticeRecipientMapper;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.service.SectorAuthorityQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityInfoDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUsersAuthoritiesInfoDTO;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserAuthorityMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorUserAuthorityInfoService {

    private final SectorUserAuthorityMapper sectorUserAuthorityMapper = Mappers.getMapper(SectorUserAuthorityMapper.class);
    private final SectorAuthorityQueryService sectorAuthorityQueryService;
    private final SectorUserInfoService sectorUserInfoService;
    private final NoticeRecipientMapper noticeRecipientMapper;

    public SectorUsersAuthoritiesInfoDTO getSectorUsersAuthoritiesInfo(AppUser appUser, Long sectorId) {
        SectorUserAuthoritiesDTO sectorUserAuthorities  = sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId);
        List<UserInfoDTO> sectorUsersInfo = getUserInfoDTOS(sectorUserAuthorities.getAuthorities());

        return getSectorUserAuthoritiesInfoDTO(sectorUserAuthorities, sectorUsersInfo);
    }

    public List<AdditionalNoticeRecipientDTO> getCandidateSectorUsersNoticeRecipients(AppUser appUser, Long sectorId) {
        SectorUserAuthoritiesDTO sectorUserAuthorities  = sectorAuthorityQueryService.getSectorUserAuthorities(appUser, sectorId);

        List<SectorUserAuthorityDTO> activeSectorUserAuthorities = sectorUserAuthorities.getAuthorities().stream()
                .filter(auth -> AuthorityStatus.ACTIVE.equals(auth.getAuthorityStatus())).toList();
        List<UserInfoDTO> sectorUsersInfo = getUserInfoDTOS(activeSectorUserAuthorities);

        return sectorUsersInfo.stream().map(noticeRecipientMapper::toSectorUSerNoticeRecipientDTO).toList();
    }

    private List<UserInfoDTO> getUserInfoDTOS(List<SectorUserAuthorityDTO> sectorUserAuthorities) {
        List<String> userIds = sectorUserAuthorities.stream().map(UserAuthorityDTO::getUserId).toList();
        return sectorUserInfoService.getSectorUsersInfo(userIds);
    }

    private SectorUsersAuthoritiesInfoDTO getSectorUserAuthoritiesInfoDTO(
            SectorUserAuthoritiesDTO sectorUserAuthorities,
            List<UserInfoDTO> sectorUsersInfo) {

        List<SectorUserAuthorityInfoDTO> sectorUserAuthorityInfoDTOS =
                sectorUserAuthorities.getAuthorities().stream()
                        .map(authority -> sectorUserAuthorityMapper.toSectorUsersAuthoritiesInfoDto(
                                        authority,
                                        sectorUsersInfo.stream()
                                                .filter(info -> info.getUserId().equals(authority.getUserId()))
                                                .findFirst()
                                                .orElse(new UserInfoDTO())))
                        .toList();

        return SectorUsersAuthoritiesInfoDTO.builder().authorities(sectorUserAuthorityInfoDTOS).editable(sectorUserAuthorities.isEditable()).build();
    }
}
