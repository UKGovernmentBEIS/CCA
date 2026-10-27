package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusCalculationRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusCalculationMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusCalculationService {

    private final BuyOutSurplusCalculationRepository buyOutSurplusCalculationRepository;
    private final FacilityDataQueryService facilityDataQueryService;
    private final TargetPeriodService targetPeriodService;
    private static final BuyOutSurplusCalculationMapper CALC_MAPPER = Mappers.getMapper(BuyOutSurplusCalculationMapper.class);

    @Transactional(readOnly = true)
    public Map<String, Map<TargetPeriodType, BuyOutSurplusCalculationDetail>> getLatestBuyOutCalculationsPerFacilityAndTargetPeriod(Long accountId, List<Long> targetPeriodIds) {
        
        List<BuyOutSurplusCalculation> calculations = buyOutSurplusCalculationRepository.findAllByAccountIdAndTargetPeriodIdIn(accountId, targetPeriodIds);

        List<FacilityBaseInfoDTO> facilities = facilityDataQueryService.getFacilitiesByAccountId(accountId);
        
        Map<Long, String> facilityBusinessIdsById = facilities.stream()
                .collect(Collectors.toMap(
                        FacilityBaseInfoDTO::getId,
                        FacilityBaseInfoDTO::getFacilityBusinessId
                ));
        
        return calculations.stream()
                .collect(Collectors.groupingBy(
                        calc -> Optional.ofNullable(facilityBusinessIdsById.get(calc.getFacilityId()))
                                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)),
                        Collectors.collectingAndThen(
                                Collectors.toMap(
                                        calc -> calc.getTargetPeriod().getBusinessId(),
                                        Function.identity(),
                                        BinaryOperator.maxBy(
                                                Comparator.comparing(BuyOutSurplusCalculation::getCreationDate))
                                ),
                                map -> map.entrySet()
                                        .stream()
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                e -> CALC_MAPPER.toBuyOutSurplusCalculationDetail(e.getValue())
                                        ))
                        )
                ));
    }
    
    /**
     * Saves the buy-out surplus calculations and returns a mapping of performance data IDs
     * to their corresponding buy-out surplus calculation IDs.
     *
     * @param details the buy-out surplus calculation details to submit
     * @param schemeVersion
     * @return a map of performance data IDs to their corresponding buy-out surplus calculation IDs,
     *         or an empty map if no details are provided
     */
    @Transactional
    public Map<Long, Long> submitBuyOutCalculations(List<BuyOutSurplusCalculationDetail> details, @NotNull SchemeVersion schemeVersion) {
        if (details.isEmpty()) {
            return Map.of();
        }
        
        List<TargetPeriod> targetPeriods = targetPeriodService.findBySchemeVersion(schemeVersion);
        Map<Long, TargetPeriod> targetPeriodMap = targetPeriods.stream()
                .collect(Collectors.toMap(
                    TargetPeriod::getId,
                    Function.identity()
                ));
        
        List<BuyOutSurplusCalculation> entities = new ArrayList<>();
        
        details.forEach(dto -> {
            TargetPeriod targetPeriod = Optional
                    .ofNullable(targetPeriodMap.get(dto.getTargetPeriodId()))
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

            BuyOutSurplusCalculation entity = CALC_MAPPER.toBuyOutSurplusCalculation(dto);
            entity.setTargetPeriod(targetPeriod);
            entities.add(entity);
        });

        List<BuyOutSurplusCalculation> savedCalculations = buyOutSurplusCalculationRepository.saveAll(entities);

        return savedCalculations.stream()
                .collect(
                Collectors.toMap(BuyOutSurplusCalculation::getPerformanceDataId, 
                        BuyOutSurplusCalculation::getId));

    }
}
