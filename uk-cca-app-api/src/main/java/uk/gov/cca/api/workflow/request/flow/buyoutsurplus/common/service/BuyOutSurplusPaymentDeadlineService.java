package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.flow.common.service.CalculateWorkingDaysService;
@Service
@RequiredArgsConstructor
public class BuyOutSurplusPaymentDeadlineService {

    private final CalculateWorkingDaysService calculateWorkingDaysService;

    public LocalDate getBuyOutPaymentDeadline(CompetentAuthorityEnum ca,
            PerformanceDataSubmissionType submissionType, 
            TargetPeriodInfoDTO currentTargetPeriod,
            LocalDate currentDate) {
        Map<TargetPeriodType, LocalDate> invoicedPaymentDeadlinePerTP = getBuyOutPaymentDeadlinePerTargetPeriod(ca, submissionType.equals(PerformanceDataSubmissionType.PRIMARY),
                List.of(currentTargetPeriod), currentTargetPeriod, currentDate);
        
        return invoicedPaymentDeadlinePerTP.get(currentTargetPeriod.getBusinessId());
    }

    /**
     * For a primary Buy-out fee, the payment deadline is a predefined, fixed date
     * corresponding to the "Buy-out fee payment deadline".
     *
     * For a secondary Buy-out fee, the payment deadline is dynamically calculated
     * as 30 working days after the notice is sent.
     */
    public Map<TargetPeriodType, LocalDate> getBuyOutPaymentDeadlinePerTargetPeriod(CompetentAuthorityEnum ca,
            boolean applyPrimaryRules, 
            List<TargetPeriodInfoDTO> targetPeriods, 
            TargetPeriodInfoDTO currentTargetPeriod,
            LocalDate currentDate) {

        if (applyPrimaryRules && targetPeriods.size() == 1) {
            return Map.of(currentTargetPeriod.getBusinessId(), currentTargetPeriod.getBuyOutPrimaryPaymentDeadline());
        }

        LocalDate secondaryPaymentDeadline = calculateWorkingDaysService.addWorkingDays(currentDate, 30, ca);

        return targetPeriods.stream()
                .collect(Collectors.toMap(TargetPeriodInfoDTO::getBusinessId,
                        tp -> applyPrimaryRules && tp.equals(currentTargetPeriod)
                                ? currentTargetPeriod.getBuyOutPrimaryPaymentDeadline()
                                : secondaryPaymentDeadline));
    }
}