package uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectorFacilityPerformanceDataReportListDTO {
    
    private List<SectorFacilityPerformanceDataReportItemDTO> performanceDataReportItems;
    private Long total;
    
    public static SectorFacilityPerformanceDataReportListDTO emptySectorFacilityPerformanceDataReportList() {
        return SectorFacilityPerformanceDataReportListDTO.builder().performanceDataReportItems(Collections.emptyList()).total(0L).build();
    }

}
