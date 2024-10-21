package uk.gov.cca.api.authorization.ccaauth.core.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaUserRoleType;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaUserRoleTypeRepository;
import uk.gov.cca.api.authorization.ccaauth.core.transform.CcaUserRoleTypeMapper;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Primary
@Service
@RequiredArgsConstructor
public class CcaUserRoleTypeService {

    private final CcaUserRoleTypeRepository userRoleTypeRepository;

    private static final CcaUserRoleTypeMapper CCA_USER_ROLE_TYPE_MAPPER= Mappers.getMapper(CcaUserRoleTypeMapper.class);

    /**
     * Returns the user role type of the provided user id.
     * @param userId the user id
     * @return {@link UserRoleTypeDTO}
     */
    public UserRoleTypeDTO getUserRoleTypeByUserId(String userId) {
        CcaUserRoleType userRoleType = userRoleTypeRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        return CCA_USER_ROLE_TYPE_MAPPER.toUserRoleTypeDTO(userRoleType);
    }

    public boolean isUserSectorUser(String userId) {
        String userRoleType = this.getUserRoleTypeByUserId(userId).getRoleType();
        return SECTOR_USER.equals(userRoleType);
    }
}
