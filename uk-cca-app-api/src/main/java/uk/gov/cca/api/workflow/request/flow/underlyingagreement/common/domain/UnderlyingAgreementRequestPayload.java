package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementRequestPayload extends CcaRequestPayload {

    private AccountReferenceData accountReferenceData;

    private UnderlyingAgreementPayload underlyingAgreement;

    private UnderlyingAgreementPayload underlyingAgreementProposed;

    @Builder.Default
    private Map<String, String> sectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> underlyingAgreementAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UnderlyingAgreementReviewGroup, UnderlyingAgreementReviewDecision> reviewGroupDecisions = new EnumMap<>(UnderlyingAgreementReviewGroup.class);

    @Builder.Default
    private Map<String, UnderlyingAgreementFacilityReviewDecision> facilitiesReviewGroupDecisions = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    private CcaDecisionNotification decisionNotification;

    private Determination determination;

    @Builder.Default
    private Map<UUID, String> underlyingAgreementActivationAttachments = new HashMap<>();

    private UnderlyingAgreementActivationDetails underlyingAgreementActivationDetails;

    private FileInfoDTO officialNotice;

    private FileInfoDTO underlyingAgreementDocument;

    @JsonIgnore
    public Set<String> getFacilityIds() {
        if (!ObjectUtils.isEmpty(underlyingAgreement)
                && !ObjectUtils.isEmpty(underlyingAgreement.getUnderlyingAgreement())) {
            return underlyingAgreement.getUnderlyingAgreement().getFacilities().stream()
                    .map(facility -> facility.getFacilityItem().getFacilityId())
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }
}
