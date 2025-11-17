package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain;

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
public class Cca3FacilityMigrationAccountState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long accountId;

    private String accountBusinessId;

    @Builder.Default
    private List<Cca3FacilityMigrationData> facilityMigrationDataList = new ArrayList<>();

    private boolean succeeded;

    private boolean cca3Participating;

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
