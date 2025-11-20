package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
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
	private static final Cca3SectorAssociationMigrationMapper MAPPER = Mappers.getMapper(Cca3SectorAssociationMigrationMapper.class);

	@Transactional
	public void updateSectorAssociationDataList(List<Cca3SectorAssociationVO> vos) {
		vos.forEach(this::updateSectorAssociationInfo);
	}

	private void updateSectorAssociationInfo(Cca3SectorAssociationVO vo) {
		UpdateSectorAssociationSchemeVO updateVo = MAPPER.toUpdateSectorAssociationSchemeDTO(vo);
		if (updateVo.getSubsectorName() != null) {
			sectorSubsectorMigrationService.migrateSubSectorAssociationToCca3Scheme(updateVo);
		} else {
			sectorSubsectorMigrationService.migrateSectorAssociationToCca3Scheme(updateVo);
		}
	}
}
