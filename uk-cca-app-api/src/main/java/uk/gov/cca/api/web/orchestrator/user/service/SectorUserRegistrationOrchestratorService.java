package uk.gov.cca.api.web.orchestrator.user.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorInvitedUserInfoDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAcceptInvitationService;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserAcceptInvitationMapper;

@Service
@RequiredArgsConstructor
public class SectorUserRegistrationOrchestratorService {

    private final SectorUserAcceptInvitationService sectorUserAcceptInvitationService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    
    private static final SectorUserAcceptInvitationMapper ACCEPT_INVITATION_MAPPER = Mappers.getMapper(SectorUserAcceptInvitationMapper.class);

    /**
     * Invites a new user to join a sector with a specified role.
     *
     * @paraminvitationToken	the invitation token
     * @return	{@link SectorInvitedUserInfoDTO}
     * 
     */
    public SectorInvitedUserInfoDTO acceptInvitation(String invitationToken) {
    	
    	SectorInvitedUserInfoDTO sectorInvitedUserInfoDTO = sectorUserAcceptInvitationService.acceptInvitation(invitationToken);
    	
    	String sector = sectorAssociationQueryService.getSectorAssociationIdentifier(sectorInvitedUserInfoDTO.getSectorAssociationId());
    	
    	return ACCEPT_INVITATION_MAPPER.toSectorInvitedUserInfoDTO(sectorInvitedUserInfoDTO, sector);
    	
    }
}
