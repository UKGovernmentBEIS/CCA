package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;

public interface BuyOutSurplusAccountProcessingTargetPeriodService {

    void processBuyOutSurplus(BuyOutSurplusAccountState accountState) throws Exception;

    TargetPeriodType getType();
}
