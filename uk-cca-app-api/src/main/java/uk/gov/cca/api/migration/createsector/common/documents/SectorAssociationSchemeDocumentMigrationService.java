package uk.gov.cca.api.migration.createsector.common.documents;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class SectorAssociationSchemeDocumentMigrationService extends MigrationBaseService {

    private final SectorAssociationRepository sectorAssociationRepository;
    private final SectorAssociationSchemeDocumentCreateMigrationService sectorAssociationSchemeDocumentCreateMigrationService;
    
    private static final String FORWARD_SLASH = "/";

    @Override
    public String getResource() {
        return "sector-association-scheme-documents";
    }

    @Override
    public List<String> migrate(String ids) {
        final List<String> results = new ArrayList<>();
        final AtomicInteger failedCounter = new AtomicInteger(0);
        
        List<SectorAssociation> sectors = sectorAssociationRepository.findAll();
        
        if(StringUtils.isNotEmpty(ids)) {
            Set<String> acronyms = Arrays.stream(ids.split(",")).collect(Collectors.toSet());
            sectors = sectors.stream()
                    .filter(sector -> acronyms.contains(sector.getAcronym()))
                    .toList();
        }
        
        sectors.forEach(sector -> results.addAll(sectorAssociationSchemeDocumentCreateMigrationService
        		.migrateUmbrellaAgreementDocument(sector.getId(), sector.getAcronym(), failedCounter)));
        
        results.add("migration of umbrella agreement documents: " + failedCounter + FORWARD_SLASH + sectors.size() + " failed");
        
        return results;
    }
}
