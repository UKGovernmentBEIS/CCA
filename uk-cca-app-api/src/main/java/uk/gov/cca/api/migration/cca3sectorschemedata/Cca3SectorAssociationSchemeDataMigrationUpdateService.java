package uk.gov.cca.api.migration.cca3sectorschemedata;

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
public class Cca3SectorAssociationSchemeDataMigrationUpdateService {

	private final Cca3SectorSubsectorSchemeDataMigrationService sectorSubsectorMigrationService;
	private static final Cca3SectorAssociationSchemeDataMigrationMapper MAPPER = Mappers.getMapper(Cca3SectorAssociationSchemeDataMigrationMapper.class);

	@Transactional
	public void updateSectorAssociationDataList(List<Cca3SectorAssociationSchemeDataVO> vos) {
		vos.forEach(this::updateSectorAssociationInfo);
	}

	private void updateSectorAssociationInfo(Cca3SectorAssociationSchemeDataVO vo) {
		UpdateSectorAssociationSchemeDataVO updateVo = MAPPER.toUpdateSectorAssociationSchemeDTO(vo);
		if (updateVo.getSubsectorName() != null) {
			sectorSubsectorMigrationService.migrateSubSectorAssociationToCca3Scheme(updateVo);
		} else {
			sectorSubsectorMigrationService.migrateSectorAssociationToCca3Scheme(updateVo);
		}
	}
}
