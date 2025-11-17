package uk.gov.cca.api.migration.cca3inprogressfacilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cca3InProgressFacilityMigrationParseResult {
    
    private List<Cca3InProgressFacilityVO> successfullyParsedInProgressFacilities;
    private int totalRecordsRead;
    private List<String> parsingErrors;
}
