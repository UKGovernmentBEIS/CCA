package uk.gov.cca.api.migration.cca3sectorassociation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3SectorAssociationMigrationValidationService {

	private final Validator validator;
	private final SectorAssociationQueryService sectorAssociationQueryService;

	public void validate(List<Cca3SectorAssociationVO> sectorAssociationInputs, List<String> errors) {

		// Validate sector acronym and subsector name are unique
		this.validateSectorSubsectorUniqueness(sectorAssociationInputs, errors);

		// Validate subsector sector definitions are the same
		this.validateSubsectorsSectorDefinitionUniqueness(sectorAssociationInputs, errors);

		for(Cca3SectorAssociationVO vo : sectorAssociationInputs) {

			// Generic data validation
			Set<ConstraintViolation<Cca3SectorAssociationVO>> violations = validator.validate(vo);
			if(!violations.isEmpty()) {
				for (ConstraintViolation<Cca3SectorAssociationVO> violation : violations) {
					errors.add(Cca3ErrorMessageUtil.constructErrorMessage(
							vo.getRowNumber(), violation.getMessage(), violation.getPropertyPath().toString())
					);
				}
			}
			// Sector and subsector validation
			this.validateSectorAndSubsectorExistence(vo, errors);

		}
	}

	private void validateSectorSubsectorUniqueness(List<Cca3SectorAssociationVO> sectorAssociationInputs, List<String> errors) {
		Map<String, Long> sectorSubsectorCounts = sectorAssociationInputs.stream()
				.map(input -> String.format("%s|%s",input.getSectorAcronym(), input.getSubsectorName()))
				.collect(Collectors.groupingBy(
						Function.identity(),
						Collectors.counting()
				));
		for (Map.Entry<String,Long> entry : sectorSubsectorCounts.entrySet()) {
			String sectorSubsectorCombination = entry.getKey();
			Long counts = entry.getValue();
			if (counts > 1) {
				errors.add(Cca3ErrorMessageUtil.constructSectorAndSubsectorCombinationError(sectorSubsectorCombination));
			}
		}
	}

	private void validateSubsectorsSectorDefinitionUniqueness(List<Cca3SectorAssociationVO> sectorAssociationInputs, List<String> errors) {

		Map<String, Set<String>> subsectorSectorDefinitions = sectorAssociationInputs.stream()
				.filter(input -> StringUtils.isNotBlank(input.getSubsectorName()))
				.collect(Collectors.groupingBy(
						Cca3SectorAssociationVO::getSectorAcronym,
						Collectors.mapping(Cca3SectorAssociationVO::getSectorDefinition, Collectors.toSet())
				));

		for (Map.Entry<String, Set<String>> entry : subsectorSectorDefinitions.entrySet()) {
			String sectorAcronym = entry.getKey();
			Set<String> sectorDefinitionsSet = entry.getValue();
			if (sectorDefinitionsSet.size() > 1) {
				errors.add(Cca3ErrorMessageUtil.constructSectorDefinitionError(sectorAcronym));
			}
		}
	}

	private void validateSectorAndSubsectorExistence(Cca3SectorAssociationVO input, List<String> errors) {
		Optional<SectorAssociation> sectorAssociation = sectorAssociationQueryService.findSectorAssociationByAcronymAndScheme(input.getSectorAcronym(), SchemeVersion.CCA_3);
		if(sectorAssociation.isEmpty()) {
			errors.add(Cca3ErrorMessageUtil
					.constructSectorSubsectorErrorMessage(input.getRowNumber(),
							"Invalid sector/sector does not exist", input.getSectorAcronym()));
			return;
		}
		Set<String> subsectorNames = sectorAssociation.get().getSubsectorAssociations().stream()
				.map(SubsectorAssociation::getName)
				.collect(Collectors.toSet());

		if(subsectorNames.isEmpty()) {
			if(!StringUtils.isBlank(input.getSubsectorName())) {
				errors.add(Cca3ErrorMessageUtil.constructSectorSubsectorErrorMessage(
						input.getRowNumber(),
						String.format("Subsector \"%s\" not valid for sector", input.getSubsectorName()),
						input.getSectorAcronym()
				));
			}
		} else {
			if(StringUtils.isBlank(input.getSubsectorName())) {
				errors.add(Cca3ErrorMessageUtil.constructSectorSubsectorErrorMessage(
						input.getRowNumber(),
						"Subsector cannot be blank for this sector", input.getSectorAcronym()
				));
			} else if(!subsectorNames.contains(input.getSubsectorName())) {
				errors.add(Cca3ErrorMessageUtil.constructSectorSubsectorErrorMessage(
						input.getRowNumber(),
						"Subsector does not exist", input.getSubsectorName()
				));
			}
		}
	}

}
