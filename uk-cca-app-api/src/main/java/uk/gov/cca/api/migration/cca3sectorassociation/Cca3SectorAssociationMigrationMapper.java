package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class Cca3SectorAssociationMigrationMapper {

	private static final int EXPECTED_COLUMNS = 9;

	public UpdateSectorAssociationSchemeVO toUpdateSectorAssociationSchemeDTO(Cca3SectorAssociationVO vo) {
		Map<String, BigDecimal> targetPeriodImprovementMap = new HashMap<>();
		targetPeriodImprovementMap.put("TP7 (2026)", MigrationUtil.toDecimal(vo.getTargetPeriod7Improvement()));
		targetPeriodImprovementMap.put("TP8 (2027-2028)", MigrationUtil.toDecimal(vo.getTargetPeriod8Improvement()));
		targetPeriodImprovementMap.put("TP9 (2029-2030)", MigrationUtil.toDecimal(vo.getTargetPeriod9Improvement()));

		return UpdateSectorAssociationSchemeVO.builder()
				.rowNumber(vo.getRowNumber())
				.sectorAcronym(vo.getSectorAcronym())
				.subsectorName(vo.getSubsectorName())
				.measurementType(vo.getMeasurementType())
				.improvementTargetsByPeriod(targetPeriodImprovementMap)
				.umaDate(vo.getUmaDate())
				.sectorDefinition(vo.getSectorDefinition())
				.build();
	}

	public List<Cca3SectorAssociationVO> toSectorAssociationVOList(String input, List<String> errors) {
		return Arrays.stream(input.strip().split("\\$\\$"))
				.map(val -> val.split("\\|"))
				.map(parts -> this.toSectorAssociationVO(parts, errors))
				.filter(Objects::nonNull)
				.toList();
	}

	private Cca3SectorAssociationVO toSectorAssociationVO(String[] values, List<String> errors) {

		if(values.length != EXPECTED_COLUMNS) {
			errors.add(Cca3ErrorMessageUtil.constructErrorMessage(values[0].strip(), "Input data not in expected format"));
			return null;
		}

		Cca3SectorAssociationVO sectorAssociation = null;
		try {
			sectorAssociation = Cca3SectorAssociationVO.builder()
					.rowNumber(Cca3SectorAssociationMigrationParser.parseLong(values[0]))
					.sectorAcronym(values[1].strip())
					.subsectorName(values[2].strip())
					.measurementType((MigrationUtil.getMeasurementTypeByDescription(values[3].strip())))
					.targetPeriod7Improvement(Cca3SectorAssociationMigrationParser.parseBigDecimal(values[4]))
					.targetPeriod8Improvement(Cca3SectorAssociationMigrationParser.parseBigDecimal(values[5]))
					.targetPeriod9Improvement(Cca3SectorAssociationMigrationParser.parseBigDecimal(values[6]))
					.umaDate(Cca3SectorAssociationMigrationParser.parseDate(values[7]))
					.sectorDefinition(values[8].strip())
					.build();
		} catch (Exception e) {
			errors.add(Cca3ErrorMessageUtil.constructErrorMessage(values[0].strip(), e.getMessage()));
		}

		return sectorAssociation;
	}
}
