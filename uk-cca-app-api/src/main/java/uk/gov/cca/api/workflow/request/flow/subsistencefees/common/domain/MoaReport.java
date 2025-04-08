package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.subsistencefees.domain.MoaType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MoaReport {

	private MoaType moaType;
	private String sectorAcronym;
	private String sectorName;
	private String businessId;
	private String operatorName;
	private LocalDate issueDate;
	private Boolean succeeded;
	@Builder.Default
	private List<String> errors = new ArrayList<>();
}
