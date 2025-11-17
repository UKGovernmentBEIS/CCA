package uk.gov.cca.api.migration.cca3sectorassociation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class Cca3SectorAssociationMigrationMapperTest {

	@InjectMocks
	Cca3SectorAssociationMigrationMapper mapper;

	@Test
	void toSectorAssociationVOList() {

		final String input = " 2 |ADS_53|  SUBSECTOR_3  |Energy (kWh)  |  10|10   |10   |  28/11/2023  | sector_definition $$   ";
		final List<String> failedEntries = new ArrayList<>();

		List<Cca3SectorAssociationVO> result = mapper.toSectorAssociationVOList(input, failedEntries);

		assertTrue(failedEntries.isEmpty());
		assertEquals(2L, result.getFirst().getRowNumber());
		assertEquals("ADS_53", result.getFirst().getSectorAcronym());
		assertEquals("SUBSECTOR_3", result.getFirst().getSubsectorName());
		assertEquals(BigDecimal.TEN, result.getFirst().getTargetPeriod7Improvement());
		assertEquals(BigDecimal.TEN, result.getFirst().getTargetPeriod8Improvement());
		assertEquals(BigDecimal.TEN, result.getFirst().getTargetPeriod9Improvement());
		assertEquals(LocalDate.of(2023,11,28), result.getFirst().getUmaDate());
	}

	@Test
	void toSectorAssociationVOList_incorrect_input() {

		final String input = " 2 |ADS_53|  SUBSECTOR_3  |Energy (kWh)  |  100|100   |100     $$   ";
		final List<String> failedEntries = new ArrayList<>();

		List<Cca3SectorAssociationVO> result = mapper.toSectorAssociationVOList(input, failedEntries);

		assertEquals( List.of(), result);
		assertFalse(failedEntries.isEmpty());
		assertTrue(failedEntries.getFirst().contains("Input data not in expected format"));
	}

	@Test
	void toSectorAssociationVOList_incorrect_input_fields() {

		final String input = " aa ||  subsector  |Energy   |  aa|bb   |cc   |  28-11-2023  |sector_definition $$   ";
		final List<String> failedEntries = new ArrayList<>();

		List<Cca3SectorAssociationVO> result = mapper.toSectorAssociationVOList(input, failedEntries);

		assertTrue(failedEntries.isEmpty());
		assertNull(result.getFirst().getRowNumber());
		assertEquals("", result.getFirst().getSectorAcronym());
		assertEquals("subsector",result.getFirst().getSubsectorName());
		assertNull(result.getFirst().getTargetPeriod7Improvement());
		assertNull(result.getFirst().getTargetPeriod8Improvement());
		assertNull(result.getFirst().getTargetPeriod9Improvement());
		assertNull(result.getFirst().getUmaDate());
	}

	@Test
	void toUpdateSectorAssociationSchemeDTO() {
		Cca3SectorAssociationVO vo = Cca3SectorAssociationVO.builder()
				.rowNumber(1L)
				.sectorAcronym("acronym")
				.subsectorName("subsector")
				.measurementType(MeasurementType.CARBON_TONNE)
				.targetPeriod7Improvement(BigDecimal.TEN)
				.targetPeriod8Improvement(BigDecimal.TEN)
				.targetPeriod9Improvement(BigDecimal.TEN)
				.umaDate(LocalDate.of(2023, 11, 25))
				.build();

		UpdateSectorAssociationSchemeVO updateVo = mapper.toUpdateSectorAssociationSchemeDTO(vo);

		assertEquals(vo.getRowNumber(), updateVo.getRowNumber());
		assertEquals(vo.getSectorAcronym(), updateVo.getSectorAcronym());
		assertEquals(vo.getSubsectorName(), updateVo.getSubsectorName());
		assertEquals(vo.getMeasurementType(), updateVo.getMeasurementType());
		assertEquals(vo.getUmaDate(), updateVo.getUmaDate());
	}
}