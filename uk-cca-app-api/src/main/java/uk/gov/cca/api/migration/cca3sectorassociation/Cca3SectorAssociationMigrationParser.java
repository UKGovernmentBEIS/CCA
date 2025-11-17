package uk.gov.cca.api.migration.cca3sectorassociation;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class Cca3SectorAssociationMigrationParser {

	public LocalDate parseDate(String date) {
		try {
			return LocalDate.parse(date.strip(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		} catch (Exception e) {
			return null;
		}
	}

	public BigDecimal parseBigDecimal(String number) {
		try {
			return new BigDecimal(number.strip());
		} catch (Exception e) {
			return null;
		}
	}

	public Long parseLong(String number) {
		try {
			return Long.parseLong(number.strip());
		} catch (Exception e) {
			return null;
		}
	}
}
