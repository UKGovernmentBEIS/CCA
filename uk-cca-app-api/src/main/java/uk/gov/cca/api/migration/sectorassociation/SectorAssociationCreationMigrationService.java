package uk.gov.cca.api.migration.sectorassociation;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccountIdentifier;
import uk.gov.cca.api.account.repository.TargetUnitAccountIdentifierRepository;
import uk.gov.cca.api.facility.domain.FacilityIdentifier;
import uk.gov.cca.api.facility.repository.FacilityIdentifierRepository;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationMapper;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class SectorAssociationCreationMigrationService {
    
    private final SectorAssociationMapper sectorAssociationMapper;
    private final SectorAssociationMigrationMapper sectorAssociationMigrationMapper = Mappers.getMapper(SectorAssociationMigrationMapper.class);

    private final SectorAssociationRepository sectorAssociationRepository;
    private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
    private final SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;
    private final TargetUnitAccountIdentifierRepository targetUnitAccountIdentifierRepository;
    private final FacilityIdentifierRepository facilityIdentifierRepository;

    @Transactional
    public void createMigratedSectorAssociation(SectorAssociationDTO sectorDTO, SectorAssociationSchemeDTO sectorSchemeDTO, List<SubsectorAssociationSchemeDTO> subSectorSchemeDTOs) {
        final SectorAssociation sector = sectorAssociationMapper.toSectorAssociation(sectorDTO);
        SectorAssociation newSector = sectorAssociationRepository.save(sector);
        
        final SectorAssociationScheme newSectorScheme = sectorAssociationMigrationMapper.toSectorAssociationScheme(sectorSchemeDTO, newSector);
        sectorAssociationSchemeRepository.save(newSectorScheme);
        
        if (!subSectorSchemeDTOs.isEmpty()) {
            List<SubsectorAssociationScheme> subsectorSchemes = new ArrayList<>();
            subSectorSchemeDTOs.forEach(dto -> subsectorSchemes.add(sectorAssociationMigrationMapper.toSubsectorAssociationScheme(dto, newSectorScheme)));
            subsectorAssociationSchemeRepository.saveAll(subsectorSchemes);
        }
        
        TargetUnitAccountIdentifier tuaId = TargetUnitAccountIdentifier.builder()
                .sectorAssociationId(newSector.getId())
                .accountId(0L)
                .build(); 
        targetUnitAccountIdentifierRepository.save(tuaId);
        
        FacilityIdentifier facilityIdentifier = FacilityIdentifier.builder()
                .sectorAssociationId(newSector.getId())
                .facilityId(0L)
                .build(); 
        facilityIdentifierRepository.save(facilityIdentifier);
    }

}
