package uk.gov.cca.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.transform.NoticeRecipientMapper;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthoritiesDTO;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.operator.service.CcaOperatorAuthorityQueryService;
import uk.gov.cca.api.user.operator.domain.OperatorAuthoritiesInfoDTO;
import uk.gov.cca.api.user.operator.domain.OperatorAuthorityInfoDTO;
import uk.gov.cca.api.user.operator.transform.OperatorAuthorityMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.user.operator.service.OperatorUserInfoService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorUserAuthorityInfoService {

    private final OperatorAuthorityMapper operatorAuthorityMapper = Mappers.getMapper(OperatorAuthorityMapper.class);
    private final CcaOperatorAuthorityQueryService operatorAuthorityQueryService;
    private final OperatorUserInfoService operatorUserInfoService;
    private final NoticeRecipientMapper noticeRecipientMapper;

    public OperatorAuthoritiesInfoDTO getOperatorAuthoritiesInfo(AppUser appUser, Long accountId) {
        OperatorAuthoritiesDTO operatorAuthorities = operatorAuthorityQueryService.getOperatorAuthorities(appUser, accountId);
        List<UserInfoDTO> usersInfo = getUserInfoDTOS(operatorAuthorities.getAuthorities());

        return getOperatorAuthoritiesInfoDTO(operatorAuthorities, usersInfo);
    }

    public List<AdditionalNoticeRecipientDTO> getCandidateOperatorNoticeRecipients(AppUser appUser, Long accountId) {
        OperatorAuthoritiesDTO operatorAuthorities = operatorAuthorityQueryService.getOperatorAuthorities(appUser, accountId);

        List<OperatorAuthorityDTO> activeOperatorAuthorities = operatorAuthorities.getAuthorities().stream()
                .filter(auth -> AuthorityStatus.ACTIVE.equals(auth.getAuthorityStatus())).toList();
        List<UserInfoDTO> usersInfo = getUserInfoDTOS(activeOperatorAuthorities);

        return usersInfo.stream().map(noticeRecipientMapper::toOperatorNoticeRecipientDTO).toList();
    }

    private List<UserInfoDTO> getUserInfoDTOS(List<OperatorAuthorityDTO> operatorAuthorities) {
        List<String> userIds = operatorAuthorities.stream().map(UserAuthorityDTO::getUserId).toList();
        return operatorUserInfoService.getOperatorUsersInfo(userIds);
    }

    private OperatorAuthoritiesInfoDTO getOperatorAuthoritiesInfoDTO(OperatorAuthoritiesDTO operatorAuthoritiesDTO, List<UserInfoDTO> userInfoDTOS) {

        List<OperatorAuthorityInfoDTO> operatorAuthorityInfoDTOS =
                operatorAuthoritiesDTO.getAuthorities().stream()
                        .map(authority -> operatorAuthorityMapper.toOperatorAuthorityInfoDto(
                                authority,
                                userInfoDTOS.stream()
                                        .filter(info -> info.getUserId().equals(authority.getUserId()))
                                        .findFirst()
                                        .orElse(new UserInfoDTO())))
                        .sorted(Comparator.comparing(OperatorAuthorityInfoDTO::getAuthorityCreationDate))
                        .toList();

        return OperatorAuthoritiesInfoDTO.builder().authorities(operatorAuthorityInfoDTOS).editable(operatorAuthoritiesDTO.isEditable()).build();
    }

}


