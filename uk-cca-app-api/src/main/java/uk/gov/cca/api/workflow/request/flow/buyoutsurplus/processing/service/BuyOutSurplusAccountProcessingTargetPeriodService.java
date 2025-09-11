package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.netz.api.workflow.request.core.domain.Request;

public interface BuyOutSurplusAccountProcessingTargetPeriodService {

    void processBuyOutSurplus(Request request, BuyOutSurplusAccountState accountState) throws BuyOutSurplusAccountProcessingException;

    TargetPeriodType getType();
}
