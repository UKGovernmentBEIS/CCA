package uk.gov.cca.api.workflow.request.flow.common.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {NoticeRecipientType.class})
public interface TargetUnitDetailsMapper {

    NoticeRecipientDTO toNoticeRecipientDTO(TargetUnitAccountContactDTO accountContactDTO, NoticeRecipientType type);

    @Mapping(target = "type", expression = "java(NoticeRecipientType.SECTOR_CONTACT)")
    NoticeRecipientDTO toSectorAssociationNoticeRecipientDTO(SectorAssociationContactDTO sectorAssociationContactDTO);
}
