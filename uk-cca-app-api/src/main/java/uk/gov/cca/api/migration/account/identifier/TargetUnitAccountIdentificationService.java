package uk.gov.cca.api.migration.account.identifier;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccountIdentifier;
import uk.gov.cca.api.account.repository.TargetUnitAccountIdentifierRepository;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetUnitAccountIdentificationService {

    private final TargetUnitAccountIdentifierRepository targetUnitAccountIdentifierRepository;
    private final SectorAssociationRepository sectorAssociationRepository;

    @Transactional
    public void updateTargetUnitAccountIdentifiers(final Map<String, Long> maxBusinessIdPerSector) {
        Map<Long, String> persistentSectors = sectorAssociationRepository.findAll()
                .stream()
                .collect(Collectors.toMap(SectorAssociation::getId, SectorAssociation::getAcronym));

        List<TargetUnitAccountIdentifier> tuIdentifiers = targetUnitAccountIdentifierRepository.findAll();
        tuIdentifiers.removeIf(tuId -> maxBusinessIdPerSector.get(persistentSectors.get(tuId.getSectorAssociationId())) == null);

        for (TargetUnitAccountIdentifier tuId : tuIdentifiers) {
            Long maxLegacyBusinessId = maxBusinessIdPerSector.get(persistentSectors.get(tuId.getSectorAssociationId()));
            Long accountId = Long.compare(tuId.getAccountId(), maxLegacyBusinessId) == 1
                            ? tuId.getAccountId()
                            : maxLegacyBusinessId;
            tuId.setAccountId(accountId);
        }

        targetUnitAccountIdentifierRepository.saveAll(tuIdentifiers);
    }
}
