package uk.gov.cca.api.workflow.request.flow.cca2termination.common.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cca2TerminationAccountState implements Serializable {
	
	@Serial
    private static final long serialVersionUID = 1L;

    private Long accountId;

    private String accountBusinessId;

    private Boolean succeeded;

    @Builder.Default
    private List<Long> facilityIds = new ArrayList<>();

    @Builder.Default
    private Long facilitiesExcluded = 0L;
    
    private boolean cca2Terminated;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
