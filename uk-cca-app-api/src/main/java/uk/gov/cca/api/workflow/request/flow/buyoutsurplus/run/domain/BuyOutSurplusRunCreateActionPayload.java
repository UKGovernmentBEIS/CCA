package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusRunCreateActionPayload extends RequestCreateActionPayload {

    @NotNull
    private TargetPeriodType targetPeriodType;
}
