package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectorAccountPerformanceDataReportListDTO {
    
    private List<SectorAccountPerformanceDataReportItemDTO> performanceDataReportItems;
    private Long total;
    
    public static SectorAccountPerformanceDataReportListDTO emptySectorAccountPerformanceDataReportList() {
        return SectorAccountPerformanceDataReportListDTO.builder().performanceDataReportItems(Collections.emptyList()).total(0L).build();
    }

}
