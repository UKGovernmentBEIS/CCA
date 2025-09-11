package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementProposedPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementReviewGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementReviewRequestTaskPayload extends UnderlyingAgreementRequestTaskPayload implements UnderlyingAgreementProposedPayload<UnderlyingAgreementPayload> {

    private UnderlyingAgreementPayload underlyingAgreementProposed;

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UnderlyingAgreementReviewGroup, UnderlyingAgreementReviewDecision> reviewGroupDecisions = new EnumMap<>(UnderlyingAgreementReviewGroup.class);

    @Builder.Default
    private Map<String, UnderlyingAgreementFacilityReviewDecision> facilitiesReviewGroupDecisions = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    private Determination determination;

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getReviewAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        final Set<UUID> reviewAttachmentIds = getReviewGroupDecisions().values().stream()
                .filter(reviewDecision -> !ObjectUtils.isEmpty(reviewDecision.getDetails()))
                .map(reviewDecision -> reviewDecision.getDetails().getFiles())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        final Set<UUID> facilitiesReviewAttachmentIds = getFacilitiesReviewGroupDecisions().values().stream()
                .filter(reviewDecision -> !ObjectUtils.isEmpty(reviewDecision.getDetails()))
                .map(reviewDecision -> reviewDecision.getDetails().getFiles())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        Set<UUID> determinationFileIds = new HashSet<>();
        Optional.ofNullable(getDetermination())
                .ifPresent(d -> determinationFileIds.addAll(d.getFiles()));

        Set<UUID> proposedReferencedAttachmentIds = getUnderlyingAgreementProposed() != null
                ? getUnderlyingAgreementProposed().getUnderlyingAgreement().getUnderlyingAgreementSectionAttachmentIds()
                : Collections.emptySet();

        return Stream.of(super.getReferencedAttachmentIds(), reviewAttachmentIds, facilitiesReviewAttachmentIds, determinationFileIds, proposedReferencedAttachmentIds)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        getUnderlyingAgreementAttachments().keySet().removeIf(uuids::contains);
        getReviewAttachments().keySet().removeIf(uuids::contains);
    }

    @JsonIgnore
    @Override
    public UnderlyingAgreementPayload getProposedUnderlyingAgreement() {
        return this.underlyingAgreementProposed;
    }
}
