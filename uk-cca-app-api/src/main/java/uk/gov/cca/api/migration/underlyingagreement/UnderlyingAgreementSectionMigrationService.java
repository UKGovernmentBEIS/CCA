package uk.gov.cca.api.migration.underlyingagreement;

import java.util.List;
import java.util.Map;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementSection;

public interface UnderlyingAgreementSectionMigrationService<T extends UnderlyingAgreementSection> {

    void populateSection(List<String> eligibleTargetUnitIds,
            Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap);

    Map<String, T> querySection(List<String> eligibleTargetUnitIds);

}
