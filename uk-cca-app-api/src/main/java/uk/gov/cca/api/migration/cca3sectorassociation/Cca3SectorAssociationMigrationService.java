package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.netz.api.common.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3SectorAssociationMigrationService extends MigrationBaseService {

	private final Cca3SectorAssociationMigrationValidationService validationService;
	private final Cca3SectorAssociationMigrationMapper mapper;
	private final Cca3SectorAssociationMigrationUpdateService updateService;

	@Override
	public List<String> migrate(String input) {

		if (StringUtils.isEmpty(input)) {
			return List.of("Please insert details for at least one sector association");
		}
		List<String> errors = new ArrayList<>();

		List<Cca3SectorAssociationVO> cca3SectorAssociationVOList = mapper.toSectorAssociationVOList(input, errors);
		validationService.validate(cca3SectorAssociationVOList, errors);

		if(errors.isEmpty()) {
			try {
				updateService.updateSectorAssociationDataList(cca3SectorAssociationVOList);
			} catch (Exception e) {
				errors.add(ExceptionUtils.getRootCause(e).getMessage());
			}
		}
		return errors;
	}

	@Override
	public String getResource() {
		return "cca3-sector-associations";
	}

}
