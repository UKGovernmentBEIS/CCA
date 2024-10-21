package uk.gov.cca.api.common.validation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessViolation {

    private String sectionName;
    private Object[] data;

    public BusinessViolation(String sectionName, Object... data) {
        this.sectionName = sectionName;
        this.data = data;
    }
}
