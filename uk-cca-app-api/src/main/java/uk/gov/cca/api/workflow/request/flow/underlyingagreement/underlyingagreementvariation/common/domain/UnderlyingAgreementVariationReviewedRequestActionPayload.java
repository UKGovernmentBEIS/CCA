package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationReviewedRequestActionPayload extends UnderlyingAgreementVariationSubmittedRequestActionPayload {

	@NotNull
    @Valid
    private CcaDecisionNotification decisionNotification;

    @Valid
    @NotEmpty
    private List<DefaultNoticeRecipient> defaultContacts;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, RequestActionUserInfo> usersInfo = new HashMap<>();

    @NotNull
    private FileInfoDTO officialNotice;

    @Valid
    @NotNull
    private Determination determination;

    @Builder.Default
    private Map<String, String> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UnderlyingAgreementVariationReviewGroup, UnderlyingAgreementReviewDecision> reviewGroupDecisions = new EnumMap<>(UnderlyingAgreementVariationReviewGroup.class);

    @Builder.Default
    private Map<String, UnderlyingAgreementVariationFacilityReviewDecision> facilitiesReviewGroupDecisions = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getFileDocuments() {
        return Stream.of(
                super.getFileDocuments(),
                Map.of(UUID.fromString(officialNotice.getUuid()), officialNotice.getName())
        ).flatMap(m -> m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), this.getReviewAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
