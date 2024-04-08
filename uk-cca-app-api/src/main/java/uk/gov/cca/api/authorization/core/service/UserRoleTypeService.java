package uk.gov.cca.api.authorization.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.UserRoleType;
import uk.gov.cca.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.cca.api.authorization.core.repository.UserRoleTypeRepository;
import uk.gov.cca.api.authorization.core.transform.UserRoleTypeMapper;
import uk.gov.netz.api.common.domain.RoleType;

import static uk.gov.netz.api.common.domain.RoleType.OPERATOR;

@Service
@RequiredArgsConstructor
public class UserRoleTypeService {

    private final UserRoleTypeRepository userRoleTypeRepository;

    private final UserRoleTypeMapper userRoleTypeMapper;

    /**
     * Returns the user role type of the provided user id.
     * @param userId the user id
     * @return {@link UserRoleTypeDTO}
     */
    public UserRoleTypeDTO getUserRoleTypeByUserId(String userId) {
        UserRoleType userRoleType = userRoleTypeRepository.findById(userId)
            .orElse(UserRoleType.builder().userId(userId).roleType(OPERATOR).build());
        return userRoleTypeMapper.toUserRoleTypeDTO(userRoleType);
    }

    /**
     * Checks if the role of the provided user is {@link RoleType#OPERATOR}.
     * @param userId the provided user
     * @return boolean value representing whether the provided user is {@link RoleType#OPERATOR} or not
     */
    public boolean isUserOperator(String userId) {
        RoleType userRoleType = getUserRoleTypeByUserId(userId).getRoleType();
        return OPERATOR.equals(userRoleType);
    }
    
    public boolean isUserRegulator(String userId) {
        return RoleType.REGULATOR == getUserRoleTypeByUserId(userId).getRoleType();
    }

    public boolean isUserVerifier(String userId) {
        return RoleType.VERIFIER == getUserRoleTypeByUserId(userId).getRoleType();
    }
}
