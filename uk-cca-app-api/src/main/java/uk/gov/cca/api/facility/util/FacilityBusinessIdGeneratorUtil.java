package uk.gov.cca.api.facility.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FacilityBusinessIdGeneratorUtil {

    public String generate(String acronym, Long facilityId) {
        return String.format("%s-F%05d", acronym, facilityId);
    }
}
