package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.service;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.common.domain.BuyOutSurplusAccountProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.common.domain.BuyOutSurplusAccountState;
import uk.gov.netz.api.workflow.request.core.domain.Request;

public interface BuyOutSurplusAccountProcessingTargetPeriodService {

    void processBuyOutSurplus(Request request, BuyOutSurplusAccountState accountState) throws BuyOutSurplusAccountProcessingException;

    TargetPeriodType getType();
}
