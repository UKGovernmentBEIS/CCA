package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaScope;
import uk.gov.cca.api.authorization.ccaauth.rules.services.SectorAssociationAuthorizationResourceService;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.user.core.domain.UserBasicInfoDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationDetailsResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.dto.SectorAssociationResponseDTO;
import uk.gov.cca.api.web.orchestrator.sectorassociation.transform.SectorAssociationDetailsResponseMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.user.core.service.auth.UserAuthService;
import uk.gov.cca.api.user.core.transform.UserBasicInfoMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectorAssociationQueryServiceOrchestrator {

    private final SectorAssociationQueryService sectorAssociationQueryService;
    private final SectorAssociationDetailsResponseMapper sectorAssociationDetailsResponseMapper;
    private final SectorAssociationAuthorizationResourceService sectorAssociationAuthorizationResourceService;
    private final UserAuthService userAuthService;
    private static final UserBasicInfoMapper USER_BASIC_INFO_MAPPER = Mappers.getMapper(UserBasicInfoMapper.class);

    public SectorAssociationResponseDTO getSectorAssociationById(Long id, AppUser user) {
        SectorAssociationDTO sectorAssociationDTO = sectorAssociationQueryService.getSectorAssociationById(id);
        SectorAssociationDetailsResponseDTO detailsResponseDTO = mapDetailsAndFetchFacilitator(sectorAssociationDTO);

        boolean isEditable = sectorAssociationAuthorizationResourceService
            .hasUserScopeToSectorAssociation(user, CcaScope.EDIT_SECTOR_ASSOCIATION, id);

        return buildResponseDTO(sectorAssociationDTO.getSectorAssociationContact(), detailsResponseDTO, isEditable);
    }

    private SectorAssociationDetailsResponseDTO mapDetailsAndFetchFacilitator(SectorAssociationDTO sectorAssociationDTO) {
        SectorAssociationDetailsDTO details = sectorAssociationDTO.getSectorAssociationDetails();

        UserBasicInfoDTO facilitatorInfo = Optional.ofNullable(details.getFacilitatorUserId())
            .map(userAuthService::getUserByUserId)
            .map(USER_BASIC_INFO_MAPPER::toUserBasicInfoDTO)
            .orElse(null);

        return sectorAssociationDetailsResponseMapper
            .toSectorAssociationResponseDTO(details, facilitatorInfo);
    }

    private SectorAssociationResponseDTO buildResponseDTO(SectorAssociationContactDTO sectorAssociationContactDTO,
                                                          SectorAssociationDetailsResponseDTO detailsResponseDTO,
                                                          boolean editable) {
        return SectorAssociationResponseDTO.builder()
            .sectorAssociationContact(sectorAssociationContactDTO)
            .sectorAssociationDetails(detailsResponseDTO)
            .editable(editable)
            .build();
    }
}
