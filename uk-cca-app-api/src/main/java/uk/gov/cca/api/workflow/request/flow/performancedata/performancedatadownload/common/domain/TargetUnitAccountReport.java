package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * should be serializable to be set as camunda variable during a process.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitAccountReport implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;

	private Long accountId;

	private String accountBusinessId;
	
	private boolean succeeded;
	
	private FileInfoDTO fileInfo;
	
	@Builder.Default
	private List<String> errors = new ArrayList<>();
	
}
