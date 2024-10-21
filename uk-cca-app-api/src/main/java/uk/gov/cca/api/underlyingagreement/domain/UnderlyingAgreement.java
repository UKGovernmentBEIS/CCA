package uk.gov.cca.api.underlyingagreement.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreement {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @NotEmpty
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull Facility> facilities = new HashSet<>();

    @NotNull
    @Valid
    private TargetPeriod5Details targetPeriod5Details;

    @NotNull
    @Valid
    private TargetPeriod6Details targetPeriod6Details;

    @NotNull
    @Valid
    private AuthorisationAndAdditionalEvidence authorisationAndAdditionalEvidence;

    @JsonIgnore
    public Set<UUID> getUnderlyingAgreementSectionAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();

        // TargetPeriod6Details
        if(targetPeriod6Details != null && !ObjectUtils.isEmpty(targetPeriod6Details.getTargetComposition())) {
            if(!ObjectUtils.isEmpty(targetPeriod6Details.getTargetComposition().getCalculatorFile())) {
                attachments.add(targetPeriod6Details.getTargetComposition().getCalculatorFile());
            }
            attachments.addAll(targetPeriod6Details.getTargetComposition().getConversionEvidences());
        }
        if(targetPeriod6Details != null && !ObjectUtils.isEmpty(targetPeriod6Details.getBaselineData())) {
            attachments.addAll(targetPeriod6Details.getBaselineData().getGreenfieldEvidences());
        }

        // TargetPeriod5Details
        if(targetPeriod5Details != null && !ObjectUtils.isEmpty(targetPeriod5Details.getDetails())) {
            if(!ObjectUtils.isEmpty(targetPeriod5Details.getDetails().getTargetComposition())) {
                if(!ObjectUtils.isEmpty(targetPeriod5Details.getDetails().getTargetComposition().getCalculatorFile())) {
                    attachments.add(targetPeriod5Details.getDetails().getTargetComposition().getCalculatorFile());
                }
                attachments.addAll(targetPeriod5Details.getDetails().getTargetComposition().getConversionEvidences());
            }
            if(!ObjectUtils.isEmpty(targetPeriod5Details.getDetails().getBaselineData())) {
                attachments.addAll(targetPeriod5Details.getDetails().getBaselineData().getGreenfieldEvidences());
            }
        }

        // Facilities
        if (!ObjectUtils.isEmpty(facilities)) {
            attachments.addAll(facilities
            		.stream()
            		.map(facility -> facility.getFacilityItem().getAttachmentIds())
            		.flatMap(Set::stream)
            		.collect(Collectors.toSet()));
        }

        // AuthorisationAndAdditionalEvidence
        if (authorisationAndAdditionalEvidence != null) {

            // Authorisation Evidence
            if (!ObjectUtils.isEmpty(authorisationAndAdditionalEvidence.getAuthorisationAttachmentIds())){
                attachments.addAll(authorisationAndAdditionalEvidence.getAuthorisationAttachmentIds());
            }

            // Additional Evidence
            if (!ObjectUtils.isEmpty(authorisationAndAdditionalEvidence.getAdditionalEvidenceAttachmentIds())){
                attachments.addAll(authorisationAndAdditionalEvidence.getAdditionalEvidenceAttachmentIds());
            }
        }

        return Collections.unmodifiableSet(attachments);
    }
}
