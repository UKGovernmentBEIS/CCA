package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusCalculationDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository.BuyOutSurplusCalculationRepository;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform.BuyOutSurplusCalculationMapper;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusCalculationService {

    private final BuyOutSurplusCalculationRepository buyOutSurplusCalculationRepository;
    private static final BuyOutSurplusCalculationMapper CALC_MAPPER = Mappers.getMapper(BuyOutSurplusCalculationMapper.class);

    public Map<String, Map<TargetPeriodType, BuyOutSurplusCalculationDTO>> getLatestBuyOutCalculationsPerFacility(Long accountId) {

        List<BuyOutSurplusCalculation> calculations = buyOutSurplusCalculationRepository.findAllByAccountId(accountId);

        return calculations.stream()
                .collect(Collectors.groupingBy(
                        BuyOutSurplusCalculation::getFacilityBusinessId,
                        Collectors.collectingAndThen(
                                Collectors.toMap(
                                        BuyOutSurplusCalculation::getTargetPeriodType,
                                        Function.identity(),
                                        BinaryOperator.maxBy(
                                                Comparator.comparing(BuyOutSurplusCalculation::getCreationDate))
                                ),
                                map -> map.entrySet()
                                        .stream()
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                e -> CALC_MAPPER.toBuyOutSurplusCalculationDTO(e.getValue())
                                        ))
                        )
                ));
    }
}
