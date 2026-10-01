package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.PerformanceAccountTemplateDataStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorPerformanceAccountTemplateDataReportItemDTO {

	private Long id;
	// account id for facility resource
	private Long parentId;
	private String businessId;
	private String name;
	private LocalDateTime submissionDate;
	private PerformanceAccountTemplateDataStatus status;

	public SectorPerformanceAccountTemplateDataReportItemDTO(Long id, String businessId, String name,
			LocalDateTime submissionDate, String status) {
		this(id, null, businessId, name, submissionDate, status);
	}

	public SectorPerformanceAccountTemplateDataReportItemDTO(Long id, Long parentId, String businessId, String name,
															 LocalDateTime submissionDate, String status) {
		this.id = id;
		this.parentId = parentId;
		this.businessId = businessId;
		this.name = name;
		this.submissionDate = submissionDate;
		this.status = PerformanceAccountTemplateDataStatus.valueOf(status);
	}
}
