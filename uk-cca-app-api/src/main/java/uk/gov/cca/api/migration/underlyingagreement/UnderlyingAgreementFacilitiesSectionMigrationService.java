package uk.gov.cca.api.migration.underlyingagreement;

import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementSection;

public interface UnderlyingAgreementFacilitiesSectionMigrationService<T extends UnderlyingAgreementSection> {

    void populateSection(List<String> filterByTargetUnitIds,
            Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap);

    Map<String, Set<T>> querySection(List<String> targetUnitIds);

}
