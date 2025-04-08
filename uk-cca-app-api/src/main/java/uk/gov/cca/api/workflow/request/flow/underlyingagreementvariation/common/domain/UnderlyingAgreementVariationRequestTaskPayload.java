package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationRequestTaskPayload extends RequestTaskPayload {

    private AccountReferenceData accountReferenceData;

    private UnderlyingAgreementContainer originalUnderlyingAgreementContainer;

    private UnderlyingAgreementVariationPayload underlyingAgreement;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> underlyingAgreementAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> reviewGroupDecisions = new EnumMap<>(UnderlyingAgreementVariationReviewGroup.class);

    @Builder.Default
    private Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilitiesReviewGroupDecisions = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getUnderlyingAgreementAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getUnderlyingAgreement() != null ?
                getUnderlyingAgreement().getUnderlyingAgreement().getUnderlyingAgreementSectionAttachmentIds() :
                Collections.emptySet();
    }

    @JsonIgnore
    public UnderlyingAgreementVariationPayload getEditedUnderlyingAgreement() {
        return this.underlyingAgreement;
    }
}
