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
public class AccountIdentifierCreationService {

    private final TargetUnitAccountIdentifierRepository targetUnitAccountIdentifierRepository;
    private final SectorAssociationRepository sectorAssociationRepository;

    @Transactional
    public void updateTargetUnitAccountIdentifiers(final Map<String, Long> maxBusinessIdPerSectorAcronym) {
        Map<Long, String> persistedSectors = sectorAssociationRepository.findAll()
                .stream()
                .collect(Collectors.toMap(SectorAssociation::getId, SectorAssociation::getAcronym));

        List<TargetUnitAccountIdentifier> tuIdentifiers = targetUnitAccountIdentifierRepository.findAll();

        for (TargetUnitAccountIdentifier tuId : tuIdentifiers) {
            Long maxAccountId = maxBusinessIdPerSectorAcronym.get(persistedSectors.get(tuId.getSectorAssociationId()));
            tuId.setAccountId(maxAccountId == null ? 0L : maxAccountId);
        }

        targetUnitAccountIdentifierRepository.saveAll(tuIdentifiers);
    }
}
