package uk.gov.cca.api.migration.cca3sectorassociation;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationSchemeRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@Validated
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3SectorSubsectorSchemeMigrationService {

	private final SectorAssociationSchemeRepository sectorAssociationSchemeRepository;
	private final SubsectorAssociationSchemeRepository subsectorAssociationSchemeRepository;


	public void migrateSectorAssociationToCca3Scheme(@Valid UpdateSectorAssociationSchemeVO updateVo) {

		SectorAssociationScheme scheme = sectorAssociationSchemeRepository
				.findBySchemeVersionAndSectorAssociation_Acronym(SchemeVersion.CCA_3, updateVo.getSectorAcronym());
		scheme.setUmaDate(updateVo.getUmaDate());
		scheme.setSectorDefinition(updateVo.getSectorDefinition());
		this.updateSchemeTargetSet(scheme.getTargetSet(), updateVo);
	}

	public void migrateSubSectorAssociationToCca3Scheme(@Valid UpdateSectorAssociationSchemeVO updateVo) {

		SubsectorAssociationScheme scheme = subsectorAssociationSchemeRepository
				.findBySchemeVersionAndSubsectorAssociation_NameAndSubsectorAssociation_SectorAssociation_Acronym(
						SchemeVersion.CCA_3, updateVo.getSubsectorName(), updateVo.getSectorAcronym());

		this.updateSectorAssociationSchemeUmaDate(updateVo.getSectorAcronym(), updateVo.getUmaDate());
		this.updateSectorAssociationSchemeSectorDefinition(updateVo.getSectorAcronym(), updateVo.getSectorDefinition());
		this.updateSchemeTargetSet(scheme.getTargetSet(), updateVo);
	}

	private void updateSchemeTargetSet(TargetSet targetSet, UpdateSectorAssociationSchemeVO updateVo) {

		targetSet.setEnergyOrCarbonUnit(updateVo.getMeasurementType().getUnit());

		for(Map.Entry<String, BigDecimal> entry: updateVo.getImprovementTargetsByPeriod().entrySet()) {
			TargetCommitment targetCommitment = targetSet.getTargetCommitments().stream()
					.filter(tc -> tc.getTargetPeriod().equals(entry.getKey()))
					.findFirst()
					.orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
			targetCommitment.setTargetImprovement(entry.getValue());
		}
	}

	private void updateSectorAssociationSchemeUmaDate(String sectorAcronym, LocalDate umaDate) {
		sectorAssociationSchemeRepository.updateUmaDate(SchemeVersion.CCA_3, sectorAcronym, umaDate);
	}

	private void updateSectorAssociationSchemeSectorDefinition(String sectorAcronym, String sectorDefinition) {
		sectorAssociationSchemeRepository.updateSectorDefinition(SchemeVersion.CCA_3, sectorAcronym, sectorDefinition);
	}
}
