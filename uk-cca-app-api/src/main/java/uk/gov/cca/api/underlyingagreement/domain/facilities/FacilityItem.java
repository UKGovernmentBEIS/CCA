package uk.gov.cca.api.underlyingagreement.domain.facilities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#facilityDetails?.participatingSchemeVersions?.contains('CCA_3') == (#cca3BaselineAndTargets != null)}",
        message = "underlyingagreement.facilities.cca3BaselineAndTargets")
public class FacilityItem {

	// The facility business id
    @NotNull
    @Size(max = 255)
    @Pattern(regexp = "^[A-Z0-9_]+-F\\d{5}$", message = "underlyingagreement.facilities.facilityId")
    private String facilityId;

    @NotNull
    @Valid
    private FacilityDetails facilityDetails;

    @NotNull
    @Valid
    private TargetUnitAccountContactDTO facilityContact;

    @NotNull
    @Valid
    private EligibilityDetailsAndAuthorisation eligibilityDetailsAndAuthorisation;

    @NotNull
    @Valid
    private FacilityExtent facilityExtent;

    @NotNull
    @Valid
    private Apply70Rule apply70Rule;

    @Valid
    private Cca3FacilityBaselineAndTargets cca3BaselineAndTargets;

    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        Set<UUID> attachments = new HashSet<>();
        if(eligibilityDetailsAndAuthorisation != null && eligibilityDetailsAndAuthorisation.getPermitFile() != null) {
            attachments.add(eligibilityDetailsAndAuthorisation.getPermitFile());
        }

        if(facilityExtent != null) {
            if (facilityExtent.getProcessFlowFile() != null) {
                attachments.add(facilityExtent.getProcessFlowFile());
            }
            if (facilityExtent.getManufacturingProcessFile() != null) {
                attachments.add(facilityExtent.getManufacturingProcessFile());
            }
            if (facilityExtent.getAnnotatedSitePlansFile() != null) {
                attachments.add(facilityExtent.getAnnotatedSitePlansFile());
            }
            if (facilityExtent.getEligibleProcessFile() != null) {
                attachments.add(facilityExtent.getEligibleProcessFile());
            }
            if (facilityExtent.getActivitiesDescriptionFile() != null) {
                attachments.add(facilityExtent.getActivitiesDescriptionFile());
            }
        }

        if(apply70Rule != null && apply70Rule.getEvidenceFile() != null) {
            attachments.add(apply70Rule.getEvidenceFile());
        }

        Optional.ofNullable(cca3BaselineAndTargets).ifPresent(bt -> {
            if(bt.getTargetComposition() != null && bt.getTargetComposition().getCalculatorFile() != null) {
                attachments.add(bt.getTargetComposition().getCalculatorFile());
            }

            if(bt.getBaselineData() != null) {
                attachments.addAll(bt.getBaselineData().getGreenfieldEvidences());
            }
        });

        return Collections.unmodifiableSet(attachments);
    }
}
