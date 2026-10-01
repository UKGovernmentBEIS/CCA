package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusCalculationDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusCalculationMapper {

    @Mapping(target = "performanceOutcome", source = "calculation.data.performanceOutcome")
    @Mapping(target = "buyOutRequired", source = "calculation.data.buyOutRequired")
    @Mapping(target = "surplusGained", source = "calculation.data.surplusGained")
    @Mapping(target = "surplusUsed", source = "calculation.data.surplusUsed")
    @Mapping(target = "previousPaidFees", source = "calculation.data.previousPaidFees")
    @Mapping(target = "previousSurplusGained", source = "calculation.data.previousSurplusGained")
    @Mapping(target = "previousSurplusUsed", source = "calculation.data.previousSurplusUsed")
    @Mapping(target = "chargeType", source = "calculation.data.chargeType")
    @Mapping(target = "invoicedBuyOutFee", source = "calculation.data.invoicedBuyOutFee")
    @Mapping(target = "schemeTotalSurplus", source = "calculation.data.schemeTotalSurplus")
    BuyOutSurplusCalculationDTO toBuyOutSurplusCalculationDTO(BuyOutSurplusCalculation calculation);
}
