package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata extends RequestMetadata {
    private String parentRequestId;
    private String accountBusinessId;
    private Boolean cca3Participating;
}
