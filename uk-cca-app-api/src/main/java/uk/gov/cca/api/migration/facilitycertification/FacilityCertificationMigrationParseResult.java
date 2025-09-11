package uk.gov.cca.api.migration.facilitycertification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityCertificationMigrationParseResult {
    
    private List<FacilityCertificationVO> parsedfacilityCertificationVOList;
    private int totalRecords;
    private List<String> parsingErrors;
}
