package uk.gov.cca.api.account.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TargetUnitAccountBusinessIdGeneratorUtil {

    public String generate(String acronym, Long accountId) {
        return String.format("%s-T%05d", acronym, accountId);
    }
}
