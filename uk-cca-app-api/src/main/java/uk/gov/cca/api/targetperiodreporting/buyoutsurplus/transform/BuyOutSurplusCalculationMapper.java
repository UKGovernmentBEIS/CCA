package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculation;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.netz.api.common.config.MapperConfig;
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusCalculationMapper {

    @Mapping(target = "data", source = "data")
    @Mapping(target = "targetPeriodId", source = "targetPeriod.id")
    @Mapping(target = "targetPeriodType", source = "targetPeriod.businessId")
    BuyOutSurplusCalculationDetail toBuyOutSurplusCalculationDetail(BuyOutSurplusCalculation calculation);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    BuyOutSurplusCalculation toBuyOutSurplusCalculation(BuyOutSurplusCalculationDetail detail);
   
    BuyOutSurplusCalculationDataContainer copyData(BuyOutSurplusCalculationDataContainer dataContainer);
}
