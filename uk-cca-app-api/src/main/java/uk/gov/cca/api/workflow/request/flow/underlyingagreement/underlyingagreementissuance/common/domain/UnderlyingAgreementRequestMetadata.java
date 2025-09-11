package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class UnderlyingAgreementRequestMetadata extends RequestMetadata {
    private SchemeVersion workflowSchemeVersion;
}
