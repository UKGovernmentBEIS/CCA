package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusEntity;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {SchemeVersion.class, PerformanceDataResourceType.class})
public interface SurplusMapper {

    SurplusDTO toSurplusDTO(SurplusEntity entity);

    SurplusEntity toSurplusEntity(SurplusCreate dto, SchemeVersion schemeVersion, PerformanceDataResourceType performanceDataResourceType);

    @Mapping(target = "hasHistory", expression = "java(!entity.getSurplusHistoryList().isEmpty())")
    SurplusGainedDTO toSurplusGainedDTO(SurplusEntity entity);

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
    SurplusCreate toSurplusCreate(SurplusUpdateDTO updateDTO, Long resourceId, AppUser appUser);
}
