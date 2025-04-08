package uk.gov.cca.api.account.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TargetUnitAccountBusinessIdUtil {

	public final String TARGET_UNIT_BUSINESS_ID_REGEX = "^(?<sector>[A-Z0-9_-]+)-T\\d{5}$"; //TODO consider unify with TargetUnitAccountBusinessIdGeneratorUtil regex 
}
