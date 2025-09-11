package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SurplusMapper {

    @Mapping(target = "targetPeriodType", source = "targetPeriod.businessId")
    SurplusDTO toSurplusDTO(SurplusEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "targetPeriod", source = "targetPeriod")
    SurplusEntity toSurplusEntity(SurplusCreate dto, TargetPeriod targetPeriod);

    @Mapping(target = "submitterId", source = "history.submitterId")
    @Mapping(target = "submitter", source = "history.submitter")
    @Mapping(target = "newSurplusGained", source = "surplusGained")
    @Mapping(target = "comments", source = "history.comments")
    SurplusHistory toSurplusHistory(SurplusCreate surplusCreate);

    @Mapping(target = "surplusGained", source = "newSurplusGained")
    SurplusHistoryDTO toSurplusHistoryDTO(SurplusHistory entity);

    @Mapping(target = "surplusGained", source = "updateDTO.newSurplusGained")
    @Mapping(target = "history.comments", source = "updateDTO.comments")
    @Mapping(target = "history.submitterId", source = "appUser.userId")
    @Mapping(target = "history.submitter", source = "appUser.fullName")
    SurplusCreate toSurplusCreate(SurplusUpdateDTO updateDTO, Long accountId, AppUser appUser);
}
