package uk.gov.cca.api.migration.sectorassociation;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccountIdentifier;
import uk.gov.cca.api.account.repository.TargetUnitAccountIdentifierRepository;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityIdentifier;
import uk.gov.cca.api.facility.repository.FacilityIdentifierRepository;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSchemeDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationSchemesDTO;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationRepository;
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
    private final SubsectorAssociationRepository subsectorAssociationRepository;
    private final SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;
    private final TargetUnitAccountIdentifierRepository targetUnitAccountIdentifierRepository;
    private final FacilityIdentifierRepository facilityIdentifierRepository;

    @Transactional
    public void createMigratedSectorAssociation(SectorAssociationDTO sectorDTO, SectorAssociationSchemeDTO sectorSchemeDTO, List<SubsectorAssociationSchemesDTO> subSectorSchemeDTOs) {
    	
    	// Convert and save Sector
        final SectorAssociation sector = sectorAssociationMapper.toSectorAssociation(sectorDTO);
        SectorAssociation newSector = sectorAssociationRepository.save(sector);
        
        // Convert and save Sector Scheme
		final SectorAssociationScheme newSectorScheme = sectorAssociationMigrationMapper
				.toSectorAssociationScheme(sectorSchemeDTO, newSector);
        sectorAssociationSchemeRepository.save(newSectorScheme);
        
        // Convert and save each Subsector and its Scheme
        subSectorSchemeDTOs.forEach(dto -> saveSubsectorAndScheme(dto, sector));
        
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

	private void saveSubsectorAndScheme(SubsectorAssociationSchemesDTO dto, SectorAssociation newSector) {
		SubsectorAssociation subSector =
						SubsectorAssociation.builder()
						.name(dto.getName())
						.sectorAssociation(newSector)
						.build();
		SubsectorAssociation newSubsector = subsectorAssociationRepository.save(subSector);
		
		final SubsectorAssociationScheme newSubSectorScheme = sectorAssociationMigrationMapper
				.toSubsectorAssociationScheme(dto.getSubsectorAssociationSchemeMap().get(SchemeVersion.CCA_2), newSubsector);
		subsectorAssociationSchemeRepository.save(newSubSectorScheme);
	}

}
