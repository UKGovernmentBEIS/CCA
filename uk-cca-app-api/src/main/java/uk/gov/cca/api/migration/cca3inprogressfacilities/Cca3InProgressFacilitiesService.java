package uk.gov.cca.api.migration.cca3inprogressfacilities;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3InProgressFacilitiesService {

    private final Cca3InProgressFacilitiesMigrationRepository cca3InProgressFacilitiesMigrationRepository;

    @Transactional(readOnly = true)
    public List<RequestTask> getUnderlyingAgreementReviewRequestTasksByTargetUnits(List<String> accountBusinessIds) {
        return cca3InProgressFacilitiesMigrationRepository.findUnderlyingAgreementReviewRequestTasksByAccountBusinessIds(accountBusinessIds);
    }

    @Transactional
    public void updateAllCca3Facilities(List<Cca3InProgressFacilityVO> facilities, List<RequestTask> tasks, List<String> errors) {
		List<Cca3InProgressFacilityVO> cca3Facilities = facilities.stream()
				.filter(vo -> vo.getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3)).toList();

        for (RequestTask task : tasks) {
            for (Cca3InProgressFacilityVO facility : cca3Facilities) {
                try {
                    updateFacilitiesInPayload((UnderlyingAgreementReviewRequestTaskPayload) task.getPayload(), facility);
                } catch (Exception e) {
                    errors.add(String.format("Row: %d | Error updating facility %s: %s",
                    		facility.getRowNumber(), facility.getFacilityId(), e.getMessage()));
                }
            }
            if (ObjectUtils.isEmpty(errors)) {
                cca3InProgressFacilitiesMigrationRepository.save(task);
            }
        }
    }

    private void updateFacilitiesInPayload(UnderlyingAgreementReviewRequestTaskPayload payload,
                                           Cca3InProgressFacilityVO facilityVO) {
        payload.getUnderlyingAgreement()
                .getUnderlyingAgreement()
                .getFacilities()
                .stream()
                .filter(facilityEntity -> facilityEntity.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3))
                .forEach(facilityEntity -> updateFacilities(facilityVO, facilityEntity));
    }

    private static void updateFacilities(Cca3InProgressFacilityVO facilityVO, Facility facilityEntity) {
        FacilityItem facilityItem = facilityEntity.getFacilityItem();
        if (!facilityItem.getFacilityId().equals(facilityVO.getFacilityId())) {
            return;
        }

        FacilityTargets targets = facilityItem.getCca3BaselineAndTargets().getFacilityTargets();
        Cca3FacilityBaselineAndTargets cca3FacilityBaselineAndTargets = facilityItem.getCca3BaselineAndTargets();

        updateFacilityTargets(facilityVO, targets);
        updateFacilityBaseline(facilityVO, cca3FacilityBaselineAndTargets);
        
    }

    private static void updateFacilityTargets(Cca3InProgressFacilityVO facilityVO, FacilityTargets targets) {
        Map<TargetImprovementType, BigDecimal> improvements = targets.getImprovements();
        improvements.put(TargetImprovementType.TP7, facilityVO.getTp7Improvement());
        improvements.put(TargetImprovementType.TP8, facilityVO.getTp8Improvement());
        improvements.put(TargetImprovementType.TP9, facilityVO.getTp9Improvement());
    }

    private static void updateFacilityBaseline(Cca3InProgressFacilityVO facilityVO, Cca3FacilityBaselineAndTargets baselineAndTargets) {
		FacilityBaselineEnergyConsumption baseline = Optional
				.ofNullable(baselineAndTargets.getFacilityBaselineEnergyConsumption())
				.orElseGet(FacilityBaselineEnergyConsumption::new);
        
        baseline.setTotalFixedEnergy(facilityVO.getTotalFixedEnergy());
        baseline.setTotalThroughput(facilityVO.getTotalThroughput());
        baseline.setThroughputUnit(facilityVO.getThroughputUnit());
        
        BigDecimal variableEnergy = facilityVO.getBaselineVariableEnergy();
        boolean hasVariableEnergy = variableEnergy != null && variableEnergy.compareTo(BigDecimal.ZERO) > 0;
        if (hasVariableEnergy) {
            baseline.setBaselineVariableEnergy(variableEnergy);
            baseline.setHasVariableEnergy(Boolean.TRUE);
            baseline.setVariableEnergyType(VariableEnergyDepictionType.TOTALS);
        } else {
            baseline.setBaselineVariableEnergy(null);
            baseline.setHasVariableEnergy(Boolean.FALSE);
            baseline.setVariableEnergyType(null);
        } 
        
        baselineAndTargets.setFacilityBaselineEnergyConsumption(baseline);
    }
}
