package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

/**
 * should be serializable to be set as camunda variable during a process.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUploadReport implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long accountId;

    private String accountBusinessId;

    private Boolean succeeded;
    
    private FileInfoDTO file; //the persisted file if succeeded
    
	@Builder.Default
	private List<String> errorFilenames = new ArrayList<>(); // contains the list of file names this report represent.
																// is empty when succeeded is true.
																// has size 1 except except for cases where multiple
																// files exist for the same account
    
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    
    @JsonIgnore
    public boolean isFailed() {
    	return BooleanUtils.isFalse(succeeded);
    }

}
