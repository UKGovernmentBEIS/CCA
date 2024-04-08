package uk.gov.cca.api.account.transform;

import org.mapstruct.Mapper;
import uk.gov.cca.api.account.domain.AccountNote;
import uk.gov.cca.api.account.domain.dto.AccountNoteDto;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountNoteMapper {

    AccountNoteDto toAccountNoteDTO(AccountNote accountNote);
}
