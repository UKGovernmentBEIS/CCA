package uk.gov.cca.api.authorization.ccaauth.sectoruser.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityDetailsRepository;
import uk.gov.cca.api.authorization.ccaauth.core.repository.CcaAuthorityRepository;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.event.SectorUserDeletionEvent;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorUserAuthorityDeletionService {

	private final ApplicationEventPublisher eventPublisher;
	private final SectorAdminExistenceValidator validator;
	private final CcaAuthorityDetailsRepository authorityDetailsRepository;
    private final CcaAuthorityRepository ccaAuthorityRepository;

	@Transactional
	public void deleteSectorUserByUserIdAndSectorAssociation(String userId, Long sectorAssociationId) {
        List<CcaAuthority> ccaAuthorities = ccaAuthorityRepository.findByUserId(userId);
        CcaAuthority ccaAuthority = getCcaAuthority(sectorAssociationId, ccaAuthorities);

        validator.validateDeletion(ccaAuthority);
        authorityDetailsRepository.deleteById(ccaAuthority.getId());

        final SectorUserDeletionEvent event = SectorUserDeletionEvent.builder()
            .userId(ccaAuthority.getUserId())
            .sectorAssociationId(sectorAssociationId)
            .existCcaAuthoritiesOnOtherSectorAssociations(
                    ccaAuthorities.stream().anyMatch(auth -> !sectorAssociationId.equals(auth.getSectorAssociationId())))
            .build();
        eventPublisher.publishEvent(event);
    }

    private static CcaAuthority getCcaAuthority(Long sectorAssociationId, List<CcaAuthority> ccaAuthorities) {
        return ccaAuthorities.stream().filter(auth -> sectorAssociationId.equals(auth.getSectorAssociationId()))
                .findFirst().orElseThrow(() -> new BusinessException(CcaErrorCode.AUTHORITY_USER_NOT_RELATED_TO_SECTOR_ASSOCIATION));
    }
}
