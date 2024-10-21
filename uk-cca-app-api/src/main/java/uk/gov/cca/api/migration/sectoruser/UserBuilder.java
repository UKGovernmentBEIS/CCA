package uk.gov.cca.api.migration.sectoruser;

import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_SECTOR_USERS_EDIT;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityPermission;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.core.transform.AppUserMapper;
import uk.gov.netz.api.authorization.core.transform.AuthorityMapper;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class UserBuilder {

    private final AuthorityRepository authorityRepository;
    private final Validator validator;

    private static final AuthorityMapper authorityMapper = Mappers.getMapper(AuthorityMapper.class);
    private static final AppUserMapper appUserMapper = Mappers.getMapper(AppUserMapper.class);

    public AppUser constructRegulatorAdministrator(UserInfoDTO userInfo, CompetentAuthorityEnum competentAuthority,
            List<String> failedEntries, Long rowId) {
        Optional<Authority> regulatorAuthorityOptional = authorityRepository
                .findByUserIdAndCompetentAuthority(userInfo.getUserId(), competentAuthority);

        if (regulatorAuthorityOptional.isEmpty()) {
            failedEntries.add(SectorUserInvitationHelper.constructErrorMessage(rowId,
                    String.format("Provided user %s has not the appropriate authorities", userInfo.getEmail())));
            return null;
        }

        Authority regulatorAuthority = regulatorAuthorityOptional.get();
        if (!hasPermissionsToManageSectorUsers(regulatorAuthority)) {
            failedEntries.add(SectorUserInvitationHelper.constructErrorMessage(rowId,
                    String.format("Provided user %s has not the appropriate permissions", userInfo.getEmail())));
            return null;
        }

        AuthorityDTO appAuthorityDTO = authorityMapper.toAuthorityDTO(regulatorAuthority);
        AppAuthority appAuthority = appUserMapper.toAppAuthority(appAuthorityDTO);
        
        return AppUser.builder()
                .userId(regulatorAuthority.getUserId())
                .roleType(REGULATOR)
                .authorities(List.of(appAuthority))
                .build();
    }

    public SectorUserInvitationDTO constructInvitedSectorUser(SectorUserInvitationVO userInvitation,
            List<String> failedEntries) {
                
        SectorUserInvitationDTO userInvited = SectorUserInvitationDTO.builder()
                .email(userInvitation.getEmail())
                .firstName(userInvitation.getFirstName())
                .lastName(userInvitation.getLastName())
                .roleCode(userInvitation.getRoleCode())
                .contactType(userInvitation.getContactType())
                .build();

        // validate invited user
        Set<ConstraintViolation<SectorUserInvitationDTO>> constraintViolations = validator.validate(userInvited);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(
                    v -> failedEntries.add(SectorUserInvitationHelper.constructErrorMessage(userInvitation.getRowId(),
                            v.getPropertyPath().iterator().next().getName() + " - " + v.getMessage())));
            return null;
        }

        return userInvited;
    }

    private boolean hasPermissionsToManageSectorUsers(Authority authority) {
        return authority.getAuthorityPermissions().stream()
                .map(AuthorityPermission::getPermission)
                .toList()
                .contains(PERM_SECTOR_USERS_EDIT);
    }

}
