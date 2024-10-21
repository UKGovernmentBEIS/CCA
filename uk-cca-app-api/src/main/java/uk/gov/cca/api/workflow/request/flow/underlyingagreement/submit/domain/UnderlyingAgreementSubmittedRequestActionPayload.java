package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UnderlyingAgreementSubmittedRequestActionPayload extends CcaRequestActionPayload {

	private AccountReferenceData accountReferenceData;

	@NotNull
    @Valid
    private UnderlyingAgreementPayload underlyingAgreement;

    @Builder.Default
    private Map<UUID, String> underlyingAgreementAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getUnderlyingAgreementAttachments();
    }
}
