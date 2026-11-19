package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.FacilityPerformanceDataWithBuyOutProcessedStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.validation.BuyOutSurplusFacilityViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityProcessingContext;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityCalculationUtil;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityEligibilityUtil;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityResultsUtil;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityProcessor {

	public void doProcessFacility(BuyOutSurplusFacilityProcessingContext processingContext,
			BuyOutSurplusFacilityAccountState accountState) {

		final String facilityBusinessId = processingContext.getFacilityBusinessId();
		final boolean applyPrimaryRules = processingContext.isApplyPrimaryRules();
		final TargetPeriodType currentTargetPeriodType = processingContext.getCurrentTargetPeriod().getBusinessId();


		if (BuyOutSurplusFacilityEligibilityUtil.notEligibleFacility(applyPrimaryRules, currentTargetPeriodType, processingContext.getPerformanceData())) {
			return;
		}

		// Missing TPRs in the secondary period, fail the facility for all TPs
		if (processingContext.isSecondaryPeriodWithMissingTPRs()) {
			BuyOutSurplusFacilityResultsUtil.markTPRsAsFailed(facilityBusinessId,
					processingContext.getPerformanceData(),
					BuyOutSurplusFacilityViolation.BuyOutSurplusFacilityViolationMessage.MISSING_TPRS.getMessage(processingContext.getMissingTPRs().keySet()),
					accountState.getFacilityPerformanceResults());
			return;
		}

		// Missing TPRs in the primary period, fail the missing TPs but still process
		// the current TP
		if (processingContext.isPrimaryPeriodWithMissingTPRs()) {
			BuyOutSurplusFacilityResultsUtil.markTPRsAsFailed(facilityBusinessId,
					processingContext.getMissingTPRs(),
					BuyOutSurplusFacilityViolation.BuyOutSurplusFacilityViolationMessage.MISSING_TPRS.getMessage(processingContext.getMissingTPRs().keySet()),
					accountState.getFacilityPerformanceResults());
		}

		// No previous unprocessed TPR, process the current TP only
		if (!processingContext.hasPreviousUnprocessedTPRs() || processingContext.isPrimaryPeriodWithMissingTPRs()) {
			try {
				processCurrentTargetPeriod(
						processingContext.getPerformanceData().get(currentTargetPeriodType),
						processingContext.getPaidOrRefundedTransactions().getOrDefault(currentTargetPeriodType, List.of()),
						processingContext.getBuyOutCalculations().get(currentTargetPeriodType),
						processingContext.getCurrentTargetPeriod(), 
						processingContext.getSurplusBucket(),
						applyPrimaryRules, 
						accountState);

			} catch (BuyOutSurplusFacilityProcessingException e) {
				BuyOutSurplusFacilityResultsUtil.markTPRsAsFailed(facilityBusinessId, 
						processingContext.getPerformanceData(),
						e.getErrorMessage(), 
						accountState.getFacilityPerformanceResults());
			}

			return;
		}

		// An earlier TPR is unprocessed, process all TPs (cascading effect)
		processTargetPeriodsCascade(processingContext, accountState);
	}

	private void processCurrentTargetPeriod(FacilityPerformanceDataWithBuyOutProcessedStatus tpr,
			List<BuyOutSurplusTransactionDetailSummary> transactions, 
			BuyOutSurplusCalculationDetail latestCalculation,
			TargetPeriodInfoDTO targetPeriodInfo, 
			BigDecimal surplusBucket, 
			boolean applyPrimaryRules,
			BuyOutSurplusFacilityAccountState accountState) throws BuyOutSurplusFacilityProcessingException {

		BuyOutSurplusFacilityPerformanceResult result = BuyOutSurplusFacilityResultsUtil.initializeFacilityPerformanceResult(tpr, targetPeriodInfo.getBuyOutCost());

		if (applyPrimaryRules) {
			BuyOutSurplusFacilityCalculationUtil.calculatePrimary(tpr, targetPeriodInfo, surplusBucket, result);
		} else {
			BuyOutSurplusFacilityCalculationUtil.calculateSecondary(tpr, targetPeriodInfo, transactions, latestCalculation, surplusBucket, result);
		}

		accountState.getFacilityPerformanceResults().add(result);
	}

	private void processTargetPeriodsCascade(BuyOutSurplusFacilityProcessingContext processingContext,
			BuyOutSurplusFacilityAccountState accountState) {

		final String facilityBusinessId = processingContext.getFacilityBusinessId();
		final TargetPeriodType currentTargetPeriodType = processingContext.getCurrentTargetPeriod().getBusinessId();
		final TargetPeriodType earliestUnprocessedTP = processingContext.getEarliestUnprocessedTargetPeriodType()
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
		BigDecimal latestSurplusBucket = processingContext.getSurplusBucket();

		boolean processingStarted = false;

		for (TargetPeriodInfoDTO targetPeriodInfo : processingContext.getTargetPeriods()) {

			if (!processingStarted && targetPeriodInfo.getBusinessId().equals(earliestUnprocessedTP)) {
				processingStarted = true;
			}

			if (processingStarted) {
				final TargetPeriodType targetPeriodType = targetPeriodInfo.getBusinessId();
				final FacilityPerformanceDataWithBuyOutProcessedStatus tpr = processingContext.getPerformanceData().get(targetPeriodType);

				// A TPR that does not exist at all should be skipped
				if (tpr == null) {
					continue;
				}

				try {
					if (targetPeriodType.equals(currentTargetPeriodType)) {
						processCurrentTargetPeriod(tpr, 
								processingContext.getPaidOrRefundedTransactions().getOrDefault(targetPeriodType, List.of()),
								processingContext.getBuyOutCalculations().get(targetPeriodType), 
								targetPeriodInfo, 
								latestSurplusBucket,
								processingContext.isApplyPrimaryRules(),
								accountState);
					} else {
					    BuyOutSurplusFacilityPerformanceResult result = BuyOutSurplusFacilityResultsUtil.initializeFacilityPerformanceResult(tpr, targetPeriodInfo.getBuyOutCost());
					    
						BuyOutSurplusFacilityCalculationUtil.calculateSecondary(
								tpr,
								targetPeriodInfo,
								processingContext.getPaidOrRefundedTransactions().getOrDefault(targetPeriodType, List.of()),
								processingContext.getBuyOutCalculations().get(targetPeriodType), 
								latestSurplusBucket,
								result);

						accountState.getFacilityPerformanceResults().add(result);
						
						latestSurplusBucket = result.getData().getSchemeTotalSurplus();
					}
				} catch (BuyOutSurplusFacilityProcessingException e) {
					BuyOutSurplusFacilityResultsUtil.markTPRsAsFailed(facilityBusinessId,
							processingContext.getPerformanceData(), 
							e.getErrorMessage(), 
							accountState.getFacilityPerformanceResults());

					return;
				}
			}
		}
	}
}
