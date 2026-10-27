package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils;

import java.util.Map;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

@UtilityClass
public class BuyOutSurplusFacilityEligibilityUtil {

    public boolean notEligibleFacility(final boolean applyPrimaryRules, final TargetPeriodType currentTargetPeriodType,
            Map<TargetPeriodType, FacilityPerformanceDataWithBuyOutProcessedStatus> tprs) {

        FacilityPerformanceDataWithBuyOutProcessedStatus currentTPR = tprs.get(currentTargetPeriodType);

        boolean hasNoUnprocessedReports = tprs.values().stream()
                .allMatch(FacilityPerformanceDataWithBuyOutProcessedStatus::isProcessed);

        boolean currentPeriodHasNoUnprocessedReport = applyPrimaryRules
                && (currentTPR == null || currentTPR.isProcessed());

        return hasNoUnprocessedReports || currentPeriodHasNoUnprocessedReport;
    }
}