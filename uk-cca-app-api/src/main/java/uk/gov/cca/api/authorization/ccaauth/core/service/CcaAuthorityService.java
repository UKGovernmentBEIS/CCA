package uk.gov.cca.api.authorization.ccaauth.core.service;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityInfoDTO;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.core.transform.CcaAuthorityMapper;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.repository.AuthorityRepository;
import uk.gov.netz.api.authorization.core.service.AuthorityAbstractService;
import uk.gov.netz.api.authorization.core.transform.AuthorityMapper;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Service
public class CcaAuthorityService extends AuthorityAbstractService<CcaAuthorityDTO> {

    private final CcaAuthorityRepository ccaAuthorityRepository;
    private final CcaAuthorityDetailsRepository authorityDetailsRepository;
    private static final CcaAuthorityMapper CCA_AUTHORITY_MAPPER = Mappers.getMapper(CcaAuthorityMapper.class);

    public CcaAuthorityService(AuthorityRepository authorityRepository,
                               AuthorityMapper authorityMapper,
                               CcaAuthorityRepository ccaAuthorityRepository,
                               CcaAuthorityDetailsRepository authorityDetailsRepository) {
        super(authorityRepository, authorityMapper);
        this.ccaAuthorityRepository = ccaAuthorityRepository;
        this.authorityDetailsRepository = authorityDetailsRepository;
    }

    @Transactional
    public void updateCcaAuthorityDetailsOrganisationName(Long authorityId, String organisationName) {
        CcaAuthorityDetails authorityDetails = getAuthorityDetails(authorityId);
        authorityDetails.setOrganisationName(organisationName);
    }

    public CcaAuthorityDetails getAuthorityDetails(Long authorityId) {
        return authorityDetailsRepository.findById(authorityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public List<CcaAuthorityDTO> getActiveAuthoritiesWithAssignedPermissions(String userId) {
        return ccaAuthorityRepository.findActiveAuthoritiesWithAssignedPermissionsByUserId(userId).stream()
            .map(CCA_AUTHORITY_MAPPER::toCcaAuthorityDTO)
            .collect(Collectors.toList());
    }
        
    public Optional<CcaAuthorityInfoDTO> findCcaAuthorityByUuidAndStatusPending(String uuid) {
        return ccaAuthorityRepository.findByUuidAndStatus(uuid, AuthorityStatus.PENDING)
            .map(CCA_AUTHORITY_MAPPER::toCcaAuthorityInfoDTO);
    }
}
