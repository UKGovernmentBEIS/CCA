package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.common.domain.CsvErrorEntry;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityPerformanceAccountTemplateDataCsvErrorEntry extends CsvErrorEntry {

    private String facilityBusinessId;
    private int rowNumber;
}
