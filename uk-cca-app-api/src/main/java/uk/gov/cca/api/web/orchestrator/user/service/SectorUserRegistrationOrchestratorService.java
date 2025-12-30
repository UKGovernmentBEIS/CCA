package uk.gov.cca.api.web.orchestrator.user.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.user.sectoruser.domain.SectorInvitedUserInfoDTO;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAcceptInvitationService;
import uk.gov.cca.api.user.sectoruser.transform.SectorUserAcceptInvitationMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class SectorUserRegistrationOrchestratorService {

    private final SectorUserAcceptInvitationService sectorUserAcceptInvitationService;
    private final SectorAssociationQueryService sectorAssociationQueryService;
    
    private static final SectorUserAcceptInvitationMapper ACCEPT_INVITATION_MAPPER = Mappers.getMapper(SectorUserAcceptInvitationMapper.class);

    /**
     * Invites a new user to join a sector with a specified role.
     * @param appUser 
     *
     * @paraminvitationToken	the invitation token
     * @return	{@link SectorInvitedUserInfoDTO}
     * 
     */
    public SectorInvitedUserInfoDTO acceptInvitation(String invitationToken, AppUser appUser) {
    	
    	SectorInvitedUserInfoDTO sectorInvitedUserInfoDTO = sectorUserAcceptInvitationService.acceptInvitation(invitationToken, appUser);
    	
    	String sector = sectorAssociationQueryService.getSectorAssociationAcronymAndName(sectorInvitedUserInfoDTO.getSectorAssociationId());
    	
    	return ACCEPT_INVITATION_MAPPER.toSectorInvitedUserInfoDTO(sectorInvitedUserInfoDTO, sector);
    	
    }
}
