package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionHistoryDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusTransactionHistoryMapper {
    
    BuyOutSurplusTransactionHistoryDTO toBuyOutSurplusTransactionHistoryDTO(BuyOutSurplusTransactionHistory history);
}
