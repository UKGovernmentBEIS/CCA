package uk.gov.cca.api.authorization.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AuthorityStatus;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.cca.api.authorization.core.repository.AuthorityRepository;
import uk.gov.cca.api.authorization.core.transform.AuthorityMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final AuthorityMapper authorityMapper;

    /**
     * Returns the list of active assigned authorities for the provided user id. Is only used under Security Context.
     *
     * @param userId Keycloak user id
     * @return List of {@link AuthorityDTO}
     */
    public List<AuthorityDTO> getActiveAuthoritiesWithAssignedPermissions(String userId) {
        return authorityRepository.findByUserIdAndStatus(userId, AuthorityStatus.ACTIVE).stream()
            .map(authorityMapper::toAuthorityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Returns the list of Authorities for the provided user id.
     *
     * @param userId Keycloak user id
     * @return List of {@link AuthorityDTO}
     */
    public List<AuthorityDTO> getAuthoritiesByUserId(String userId) {
        return authorityRepository.findByUserId(userId).stream()
            .map(authorityMapper::toAuthorityDTO)
            .collect(Collectors.toList());
    }

    /**
     * Find the list of assigned permissions for the given user id.
     *
     * @param userId Keycloak user id
     * @return the permissions of the user with the given id
     */
    public List<String> findAssignedPermissionsByUserId(String userId) {
        return authorityRepository.findAssignedPermissionsByUserId(userId);
    }

    public boolean existsByUserId(String userId) {
        return authorityRepository.existsByUserId(userId);
    }

    public boolean existsByUserIdAndAccountId(String userId, Long accountId) {
        return authorityRepository
            .findByUserIdAndAccountId(userId, accountId)
            .isPresent();
    }

    public Optional<AuthorityInfoDTO> findAuthorityByUuidAndStatusPending(String uuid) {
        return authorityRepository.findByUuidAndStatus(uuid, AuthorityStatus.PENDING)
            .map(authorityMapper::toAuthorityInfoDTO);
    }

    public Map<String, AuthorityStatus> findStatusByUsersAndAccountId(List<String> userIds, Long accountId) {
        return authorityRepository.findStatusByUsersAndAccountId(userIds, accountId);
    }
    
    public Map<String, AuthorityStatus> findStatusByUsers(List<String> userIds) {
        return authorityRepository.findStatusByUsers(userIds);
    }
    
    public Optional<AuthorityInfoDTO> findAuthorityByUserIdAndAccountId(String userId, Long accountId) {
        return authorityRepository.findByUserIdAndAccountId(userId, accountId)
            .map(authorityMapper::toAuthorityInfoDTO);
    }
}
