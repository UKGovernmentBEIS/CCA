package uk.gov.cca.api.migration.underlyingagreement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.underlyingagreement.authorization.AuthorisationAndAdditionalEvidenceSectionMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.baselinetargets.TargetPeriod5DetailsSectionMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.baselinetargets.TargetPeriod6DetailsSectionMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.details.UnderlyingAgreementDetailsMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.document.UnderlyingAgreementDocumentMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.facilities.FacilitiesSectionMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.facilities.FacilityCreatedDateMigrationService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class UnderlyingAgreementBuilderService {
    
    private final TargetPeriod5DetailsSectionMigrationService targetPeriod5SectionMigrationService;
    private final TargetPeriod6DetailsSectionMigrationService targetPeriod6SectionMigrationService;
    private final FacilitiesSectionMigrationService facilitiesSectionMigrationService;
    private final AuthorisationAndAdditionalEvidenceSectionMigrationService authorisationSectionMigrationService;
    private final UnderlyingAgreementDetailsMigrationService underlyingAgreementDetailsMigrationService;
    private final UnderlyingAgreementDocumentMigrationService underlyingAgreementDocumentMigrationService;
    private final FacilityCreatedDateMigrationService facilityCreatedDateMigrationService;
    
    
    public Map<String, UnderlyingAgreementMigrationContainer> buildUnderlyingAgreements(Map<String, TargetUnitAccount> migratedAccounts) {
        if(MapUtils.isEmpty(migratedAccounts)) {
            return Map.of();
        }
        
        Map<String, UnderlyingAgreementMigrationContainer> unaMigrationContainerPerAccount = new HashMap<>();
        initializeUnderlyingAgreements(migratedAccounts.values(), unaMigrationContainerPerAccount);
        
        List<String> eligibleTargetUnitIds = migratedAccounts.keySet().stream().toList();

        targetPeriod5SectionMigrationService.populateSection(eligibleTargetUnitIds, unaMigrationContainerPerAccount);
        targetPeriod6SectionMigrationService.populateSection(eligibleTargetUnitIds, unaMigrationContainerPerAccount);
        facilitiesSectionMigrationService.populateSection(eligibleTargetUnitIds, unaMigrationContainerPerAccount);
        authorisationSectionMigrationService.populateSection(eligibleTargetUnitIds, unaMigrationContainerPerAccount);
        underlyingAgreementDetailsMigrationService.populate(eligibleTargetUnitIds, unaMigrationContainerPerAccount);
        underlyingAgreementDocumentMigrationService.populate(unaMigrationContainerPerAccount);
        facilityCreatedDateMigrationService.populate(eligibleTargetUnitIds, unaMigrationContainerPerAccount);
        
        return unaMigrationContainerPerAccount;
    }
    
    private void initializeUnderlyingAgreements(Collection<TargetUnitAccount> migratedAccounts, Map<String, UnderlyingAgreementMigrationContainer> underlyingAgreements) {
        migratedAccounts.forEach(account -> underlyingAgreements.put(account.getBusinessId(),
                UnderlyingAgreementMigrationContainer.builder()
                        .persistentAccountId(account.getId())
                        .underlyingAgreementContainer(UnderlyingAgreementContainer.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder().build())
                                .build())
                        .build()));
    }
    
}
