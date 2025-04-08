package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.workflow.request.core.domain.RequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BuyOutSurplusRunRequestMetadata extends RequestMetadata {

    private TargetPeriodType targetPeriodType;
    private Integer totalAccounts;
    private Integer failedAccounts;
}
