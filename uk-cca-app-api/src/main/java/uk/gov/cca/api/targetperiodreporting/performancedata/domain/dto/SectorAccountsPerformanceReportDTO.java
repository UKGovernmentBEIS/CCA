package uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto;

import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectorAccountsPerformanceReportDTO {
    
    private List<SectorAccountsPerformanceReportItemDTO> performanceReportItems;
    private Long total;
    
    public static SectorAccountsPerformanceReportDTO emptySectorAccountsPerformanceReport() {
        return SectorAccountsPerformanceReportDTO.builder().performanceReportItems(Collections.emptyList()).total(0L).build();
    }

}
