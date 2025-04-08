package uk.gov.cca.api.migration.underlyingagreement.facilityidentifier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.cca.api.facility.domain.FacilityIdentifier;
import uk.gov.cca.api.facility.repository.FacilityIdentifierRepository;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class FacilityIdentificationService {

    private final FacilityIdentifierRepository facilitydentifierRepository;
    private final SectorAssociationRepository sectorAssociationRepository;

    @Transactional
    public void updateFacilityIdentifiers(final Map<String, Long> maxBusinessIdPerSector) {
        Map<Long, String> persistentSectors = sectorAssociationRepository.findAll()
                .stream()
                .collect(Collectors.toMap(SectorAssociation::getId, SectorAssociation::getAcronym));

        List<FacilityIdentifier> facilityIdentifiers = facilitydentifierRepository.findAll();
        facilityIdentifiers.removeIf(tuId -> maxBusinessIdPerSector.get(persistentSectors.get(tuId.getSectorAssociationId())) == null);

        for (FacilityIdentifier facilityIdentifier : facilityIdentifiers) {
            Long maxLegacyBusinessId = maxBusinessIdPerSector.get(persistentSectors.get(facilityIdentifier.getSectorAssociationId()));
            Long facilityId = Long.compare(facilityIdentifier.getFacilityId(), maxLegacyBusinessId) == 1
                            ? facilityIdentifier.getFacilityId()
                            : maxLegacyBusinessId;
            facilityIdentifier.setFacilityId(facilityId);
        }

        facilitydentifierRepository.saveAll(facilityIdentifiers);
    }
}
