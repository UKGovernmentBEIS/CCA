package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class FacilityCertificationAccountState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long accountId;

    private String accountBusinessId;

    private Long lastPerformanceDataId;

    private boolean succeeded;

    @Builder.Default
    private List<Long> facilityIds = new ArrayList<>();

    private Long facilitiesCertified;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
