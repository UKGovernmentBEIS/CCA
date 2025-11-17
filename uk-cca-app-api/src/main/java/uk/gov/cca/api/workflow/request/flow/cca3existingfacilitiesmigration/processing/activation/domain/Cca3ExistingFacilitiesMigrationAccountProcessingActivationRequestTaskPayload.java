package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload extends RequestTaskPayload {

    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails activationDetails;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> activationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getActivationAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return Optional.ofNullable(getActivationDetails())
                .map(Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails::getEvidenceFiles)
                .orElse(Collections.emptySet());
    }
}
