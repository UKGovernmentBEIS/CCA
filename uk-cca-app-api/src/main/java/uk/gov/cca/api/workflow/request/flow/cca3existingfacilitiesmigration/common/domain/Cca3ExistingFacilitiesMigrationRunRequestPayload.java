package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Cca3ExistingFacilitiesMigrationRunRequestPayload extends RequestPayload {

    private String defaultSignatory;

    @Builder.Default
    private Map<Long, Cca3FacilityMigrationAccountState> accountStates = new HashMap<>();

    private String csvSourceFile;
}
