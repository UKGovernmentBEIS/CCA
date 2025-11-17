package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.migration.MigrationEndpoint;

import java.util.List;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3SectorAssociationMigrationUpdateService {


	private final Cca3SectorSubsectorSchemeMigrationService sectorSubsectorMigrationService;
	private final Cca3SectorAssociationMigrationMapper mapper;

	@Transactional
	public void updateSectorAssociationDataList(List<Cca3SectorAssociationVO> vos) {

		vos.forEach(this::updateSectorAssociationInfo);
	}

	private void updateSectorAssociationInfo(Cca3SectorAssociationVO vo) {

		UpdateSectorAssociationSchemeVO updateVo = mapper.toUpdateSectorAssociationSchemeDTO(vo);
		if (updateVo.getSubsectorName().isBlank()) {
			sectorSubsectorMigrationService.migrateSectorAssociationToCca3Scheme(updateVo);
		} else {
			sectorSubsectorMigrationService.migrateSubSectorAssociationToCca3Scheme(updateVo);
		}
	}
}
