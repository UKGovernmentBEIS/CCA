package uk.gov.cca.api.authorization.ccaauth.core.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.netz.api.authorization.core.repository.RoleRepository;
import uk.gov.netz.api.authorization.core.transform.RoleMapper;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@Service
@AllArgsConstructor
@Builder
public class CcaRoleService {

    private static final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);
    private final RoleRepository roleRepository;

    public List<RoleDTO> getSectorUserRoles() {
        return roleRepository.findByType(SECTOR_USER).stream().map(roleMapper::toRoleDTO).collect(Collectors.toList());
    }
}
