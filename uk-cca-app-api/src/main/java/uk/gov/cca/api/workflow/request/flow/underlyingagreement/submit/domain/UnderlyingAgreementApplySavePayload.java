package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain;

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
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnderlyingAgreementApplySavePayload {

    @NotNull
    @Valid
    private UnderlyingAgreementApplySaveTargetUnitDetails underlyingAgreementTargetUnitDetails;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @Valid
    @NotEmpty
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<@NotNull FacilityItem> facilities = new HashSet<>();

    @NotNull
    @Valid
    private TargetPeriod5Details targetPeriod5Details;

    @NotNull
    @Valid
    private TargetPeriod6Details targetPeriod6Details;

    @NotNull
    @Valid
    private AuthorisationAndAdditionalEvidence authorisationAndAdditionalEvidence;
}
