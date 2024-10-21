package uk.gov.cca.api.sectorassociation.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsUpdateDTO;
import uk.gov.cca.api.sectorassociation.transform.LocationMapper;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class SectorAssociationUpdateService {

    private final SectorAssociationRepository sectorAssociationRepository;
    private final LocationMapper locationMapper;

    @Transactional
    public void updateSectorAssociationDetails(Long sectorAssociationId,
                                               @Valid SectorAssociationDetailsUpdateDTO sectorAssociationDetailsUpdateDTO) {

        SectorAssociation sectorAssociation = sectorAssociationRepository.findById(sectorAssociationId).orElseThrow(()
            -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        sectorAssociation.setName(sectorAssociationDetailsUpdateDTO.getCommonName());
        sectorAssociation.setLegalName(sectorAssociationDetailsUpdateDTO.getLegalName());

        Location sectorAssociationLocation = sectorAssociation.getLocation();

        Location sectorAssociationLocationUpdate = locationMapper
            .addressDTOToLocation(sectorAssociationDetailsUpdateDTO.getNoticeServiceAddress());

        sectorAssociationLocation.setLine1(sectorAssociationLocationUpdate.getLine1());
        sectorAssociationLocation.setLine2(sectorAssociationLocationUpdate.getLine2());
        sectorAssociationLocation.setCounty(sectorAssociationLocationUpdate.getCounty());
        sectorAssociationLocation.setCity(sectorAssociationLocationUpdate.getCity());
        sectorAssociationLocation.setPostcode(sectorAssociationLocationUpdate.getPostcode());

        sectorAssociation.setLocation(sectorAssociationLocationUpdate);
    }

    @Transactional
    public void updateSectorAssociationContact(Long sectorAssociationId,
                                               @Valid SectorAssociationContactDTO sectorAssociationContactDTO) {

        SectorAssociation sectorAssociation = sectorAssociationRepository.findById(sectorAssociationId).orElseThrow(()
            -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        SectorAssociationContact sectorAssociationContact = sectorAssociation.getSectorAssociationContact();

        sectorAssociationContact.setTitle(sectorAssociationContactDTO.getTitle());
        sectorAssociationContact.setFirstName(sectorAssociationContactDTO.getFirstName());
        sectorAssociationContact.setLastName(sectorAssociationContactDTO.getLastName());
        sectorAssociationContact.setJobTitle(sectorAssociationContactDTO.getJobTitle());
        sectorAssociationContact.setOrganisationName(sectorAssociationContactDTO.getOrganisationName());
        sectorAssociationContact.setPhoneNumber(sectorAssociationContactDTO.getPhoneNumber());
        sectorAssociationContact.setEmail(sectorAssociationContactDTO.getEmail());

        Location sectorAssociationContactLocation = sectorAssociationContact.getLocation();

        Location sectorAssociationContactLocationUpdate = locationMapper.addressDTOToLocation(sectorAssociationContactDTO.getAddress());

        sectorAssociationContactLocation.setLine1(sectorAssociationContactLocationUpdate.getLine1());
        sectorAssociationContactLocation.setLine2(sectorAssociationContactLocationUpdate.getLine2());
        sectorAssociationContactLocation.setCounty(sectorAssociationContactLocationUpdate.getCounty());
        sectorAssociationContactLocation.setCity(sectorAssociationContactLocationUpdate.getCity());
        sectorAssociationContactLocation.setPostcode(sectorAssociationContactLocationUpdate.getPostcode());

        sectorAssociationContact.setLocation(sectorAssociationContactLocationUpdate);
    }
}
