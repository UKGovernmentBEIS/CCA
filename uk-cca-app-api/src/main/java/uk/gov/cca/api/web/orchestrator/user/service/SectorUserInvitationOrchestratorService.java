package uk.gov.cca.api.web.orchestrator.user.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserInvitationDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserInvitationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class SectorUserInvitationOrchestratorService {

    private final SectorUserInvitationService sectorUserInvitationService;
    private final SectorAssociationQueryService sectorAssociationQueryService;

    /**
     * Invites a new user to join a sector with a specified role.
     *
     * @param sectorAssociationId     the sector id
     * @param sectorUserInvitationDTO the {@link SectorUserInvitationDTO}
     * @param currentUser             the current logged-in {@link AppUser}
     */
    @Transactional
    public void inviteUserToSectorAssociation(Long sectorAssociationId, SectorUserInvitationDTO sectorUserInvitationDTO, AppUser currentUser) {
    	
    	String sectorAssociationName = sectorAssociationQueryService.getSectorAssociationIdentifier(sectorAssociationId);
    	Objects.requireNonNull(sectorAssociationName);
    	
        sectorUserInvitationService.inviteUserToSectorAssociation(sectorAssociationId, sectorAssociationName, sectorUserInvitationDTO, currentUser);
   
    }
}
