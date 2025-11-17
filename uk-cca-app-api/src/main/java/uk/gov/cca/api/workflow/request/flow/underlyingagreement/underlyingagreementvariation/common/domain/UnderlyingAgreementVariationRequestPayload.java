package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementReviewDecision;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationRequestPayload extends CcaRequestPayload {

	private SchemeVersion workflowSchemeVersion;

	private Map<SchemeVersion, Integer> underlyingAgreementVersionMap;
	
	private AccountReferenceData accountReferenceData;

	private UnderlyingAgreementContainer originalUnderlyingAgreementContainer;
	
	private UnderlyingAgreementVariationPayload underlyingAgreement;

	private UnderlyingAgreementVariationPayload underlyingAgreementProposed;

	private CcaDecisionNotification decisionNotification;

    private UnderlyingAgreementActivationDetails underlyingAgreementActivationDetails;

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

	@Builder.Default
	private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> underlyingAgreementActivationAttachments = new HashMap<>();

	private Determination determination;
	
	private List<FileInfoDTO> officialNotices;
    
	private Map<SchemeVersion, FileInfoDTO> underlyingAgreementDocuments;

	@JsonIgnore
	public Set<String> getFacilityIds() {
		if(!ObjectUtils.isEmpty(underlyingAgreement)
				&& !ObjectUtils.isEmpty(underlyingAgreement.getUnderlyingAgreement())) {
			return underlyingAgreement.getUnderlyingAgreement().getFacilities().stream()
					.map(facility -> facility.getFacilityItem().getFacilityId())
					.collect(Collectors.toSet());
		}
		return Set.of();
	}
}
