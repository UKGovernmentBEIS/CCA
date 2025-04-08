package uk.gov.cca.api.buyoutsurplus.transform;

import org.mapstruct.Mapper;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutSurplusEntity;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.cca.api.buyoutsurplus.domain.dto.BuyOutSurplusDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusMapper {

    BuyOutSurplusEntity toBuyOutFeeTransactionEntity(BuyOutSurplusDTO dto);
}
