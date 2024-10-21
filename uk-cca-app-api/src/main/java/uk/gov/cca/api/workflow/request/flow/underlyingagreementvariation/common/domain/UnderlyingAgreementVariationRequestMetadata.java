package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementVariationRequestMetadata extends RequestMetadata {

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer underlyingAgreementVersion;
}
