package uk.gov.cca.api.notification.template.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.notification.template.domain.NotificationTemplate;
import uk.gov.cca.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", uses = TemplateInfoMapper.class, config = MapperConfig.class)
public interface NotificationTemplateMapper {

    @Mapping(target = "name", source = "name.name")
    NotificationTemplateDTO toNotificationTemplateDTO(NotificationTemplate notificationTemplate);
}
