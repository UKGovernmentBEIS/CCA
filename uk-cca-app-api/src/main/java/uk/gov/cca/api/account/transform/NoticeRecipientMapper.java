package uk.gov.cca.api.account.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.cca.api.account.domain.dto.AdditionalNoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {NoticeRecipientType.class})
public interface NoticeRecipientMapper {

    @Mapping(target = "type", expression = "java(NoticeRecipientType.OPERATOR)")
    AdditionalNoticeRecipientDTO toOperatorNoticeRecipientDTO(UserInfoDTO userInfoDTO);

    @Mapping(target = "type", expression = "java(NoticeRecipientType.SECTOR_USER)")
    AdditionalNoticeRecipientDTO toSectorUSerNoticeRecipientDTO(UserInfoDTO userInfoDTO);
}
