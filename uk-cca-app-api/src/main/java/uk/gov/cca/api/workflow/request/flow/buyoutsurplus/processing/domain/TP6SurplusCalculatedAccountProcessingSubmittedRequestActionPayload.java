package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TP6SurplusCalculatedAccountProcessingSubmittedRequestActionPayload extends TP6BuyOutSurplusAccountProcessingSubmittedRequestActionPayload {

    @Valid
    @NotNull
    private SurplusCalculatedDetails surplusCalculatedDetails;
}
