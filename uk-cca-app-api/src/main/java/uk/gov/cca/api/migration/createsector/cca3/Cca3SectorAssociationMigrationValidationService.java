package uk.gov.cca.api.migration.createsector.cca3;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3SectorAssociationMigrationValidationService {
	
	private final DataValidator<Cca3SectorAssociationVO> dataValidator;
	private final SectorAssociationQueryService sectorAssociationQueryService;
	
	private static final LocalDate FIXED_UMA_DATE = LocalDate.of(2027, 1, 1);
	
	public void validateCsvData(List<Cca3SectorAssociationVO> sectorInputs, List<String> errors) {
		// Validate sector acronym unique in file, and does not already exist
		validateSectorAcronym(sectorInputs, errors);

		for(Cca3SectorAssociationVO sectorInput : sectorInputs) {
			
			// Validate uma date cannot be earlier than 01/01/2027
			validateUmaDate(sectorInput, errors);
			
			// Generic data validation
			dataValidator.validate(sectorInput)
					.map(violation -> Arrays.stream(violation.getData())
							.map(d -> String.format("%s: %s", sectorInput.getAcronym(), d.toString()))
							.toList())
					.ifPresent(errors::addAll);
		}
		
	}

	private void validateUmaDate(Cca3SectorAssociationVO sectorInput, List<String> errors) {
		if (sectorInput.getUmaDate() != null && sectorInput.getUmaDate().isBefore(FIXED_UMA_DATE)) {
			errors.add("Uma date cannot be earlier than 01/01/2027 for sector: " + sectorInput.getAcronym());
		}
		
	}

	private void validateSectorAcronym(List<Cca3SectorAssociationVO> sectorInputs, List<String> errors) {
		Map<String, Long> acronymCount = sectorInputs.stream()
			    .map(Cca3SectorAssociationVO::getAcronym)
			    .collect(Collectors.groupingBy(
			        Function.identity(),
			        Collectors.counting()
			    ));

			Map<String, SectorAcronymValidationResult> validationResults =
					acronymCount.keySet().stream()
			        .collect(Collectors.toMap(
			            Function.identity(),
			            acronym -> new SectorAcronymValidationResult(
			            	acronymCount.get(acronym) > 1,
			            	sectorAssociationQueryService.getSectorAssociationIdByAcronym(acronym).isPresent()
			            )
			        ));
			
			validationResults.forEach((acronym, result) -> {
			    if (result.duplicated()) {
			        errors.add("Duplicate sector acronym in csv file: " + acronym);
			    }

			    if (result.alreadyExists()) {
			        errors.add("Sector acronym already exists: " + acronym);
			    }
			});
		
	}
	
	private record SectorAcronymValidationResult(
		    boolean duplicated,
		    boolean alreadyExists
		) {}

}
