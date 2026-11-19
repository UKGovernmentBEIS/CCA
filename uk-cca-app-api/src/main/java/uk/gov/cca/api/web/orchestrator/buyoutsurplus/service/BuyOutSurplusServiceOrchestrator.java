package uk.gov.cca.api.web.orchestrator.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.web.orchestrator.buyoutsurplus.dto.AvailableTargetPeriodsBuyOutDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodBuyOutDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.transform.TargetPeriodMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusServiceOrchestrator {

    private final TargetPeriodService targetPeriodService;
    private final List<BuyOutSurplusQueryService> buyOutSurplusQueryServices;
    private final SchemeTerminationHelper schemeTerminationHelper;
    private static final TargetPeriodMapper TARGET_PERIOD_MAPPER = Mappers.getMapper(TargetPeriodMapper.class);

    public List<TargetUnitAccountBusinessInfoDTO> getExcludedAccountsForBuyOutSurplusRun(TargetPeriodType targetPeriodType) {
        SchemeVersion schemeVersion = targetPeriodService.getTargetPeriodInfoByTargetPeriodType(targetPeriodType).getSchemeVersion();

        return buyOutSurplusQueryServices.stream()
                .filter(service -> service.getResourceType().equals(schemeVersion.getResourceType())).findFirst()
                .map(service -> service.getExcludedEligibleAccountsForBuyOutSurplusByTargetPeriod(targetPeriodType))
                .orElse(List.of());
    }

    public AvailableTargetPeriodsBuyOutDTO getAvailableBuyOutTargetPeriods(LocalDate date) {
        List<TargetPeriodBuyOutDetailsDTO> currentTps = new ArrayList<>();
        List<TargetPeriodBuyOutDetailsDTO> previousTps = new ArrayList<>();

        List<TargetPeriod> periods = targetPeriodService.getTargetPeriodBuyOutCurrentAndPrevious(date);

        // TP6 might overlap with current TP
        getAvailableBuyOutTargetPeriodsForCca2(date, periods, currentTps);

        getAvailableBuyOutTargetPeriodsForCca3(periods, currentTps, previousTps);

        return AvailableTargetPeriodsBuyOutDTO.builder()
                .currentTargetPeriods(currentTps)
                .previousTargetPeriods(previousTps)
                .build();
    }

    private void getAvailableBuyOutTargetPeriodsForCca3(List<TargetPeriod> periods, List<TargetPeriodBuyOutDetailsDTO> currentTps,
                                                        List<TargetPeriodBuyOutDetailsDTO> previousTps) {
        List<TargetPeriod> cca3Periods = periods.stream()
                .filter(tp -> SchemeVersion.CCA_3.equals(tp.getSchemeVersion()))
                .toList();

        if (cca3Periods.isEmpty()) {
            return;
        }

        // Get current TP
        currentTps.add(TARGET_PERIOD_MAPPER.toTargetPeriodBuyOutDetailsDTO(cca3Periods.getFirst()));

        // Get previous TPs
        previousTps.addAll(cca3Periods.stream()
                .skip(1)
                .map(TARGET_PERIOD_MAPPER::toTargetPeriodBuyOutDetailsDTO)
                .toList());
    }

    private void getAvailableBuyOutTargetPeriodsForCca2(LocalDate date, List<TargetPeriod> periods,
                                                        List<TargetPeriodBuyOutDetailsDTO> currentTps) {
        if (!schemeTerminationHelper.isAfterCca2TerminationDate(date)) {
            periods.stream()
                    .filter(tp -> tp.getBusinessId() == TargetPeriodType.TP6)
                    .findFirst()
                    .map(TARGET_PERIOD_MAPPER::toTargetPeriodBuyOutDetailsDTO)
                    .ifPresent(currentTps::add);
        }
    }
}
