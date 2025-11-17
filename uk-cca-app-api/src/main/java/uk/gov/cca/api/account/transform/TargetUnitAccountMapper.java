package uk.gov.cca.api.account.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountContact;
import uk.gov.cca.api.account.domain.TargetUnitAccountContactType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountHeaderInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AccountAddressMapper.class}, config = MapperConfig.class)
public interface TargetUnitAccountMapper {

    TargetUnitAccountHeaderInfoDTO toTargetUnitAccountHeaderInfoDTO(TargetUnitAccount account);

    @Mapping(target = "accountId", source = "id")
    TargetUnitAccountBusinessInfoDTO toTargetUnitAccountBusinessInfoDTO(TargetUnitAccount account);

    @Mapping(target = "id", source = "accountId")
    TargetUnitAccount toTargetUnitAccount(TargetUnitAccountDTO accountDTO, Long accountId);

    @Mapping(target = "responsiblePerson", source = "targetUnitAccountContacts", qualifiedByName = "responsiblePersonToDTO")
    @Mapping(target = "administrativeContactDetails", source = "targetUnitAccountContacts", qualifiedByName = "administrativeContactDetailsToDTO")
    TargetUnitAccountDTO toTargetUnitAccountDTO(TargetUnitAccount account);
    
    @Mapping(target = "address", ignore = true)
    TargetUnitAccountDTO toNoContactsTargetUnitAccountDTO(TargetUnitAccount account);

    @Mapping(target = "responsiblePerson", source = "targetUnitAccountContacts", qualifiedByName = "responsiblePersonToDTO")
    @Mapping(target = "administrativeContactDetails", source = "targetUnitAccountContacts", qualifiedByName = "administrativeContactDetailsToDTO")
    TargetUnitAccountDetailsDTO toTargetUnitAccountDetailsDTO(TargetUnitAccount account);

    @Mapping(target = "phoneNumber", source = "targetUnitAccountContactDTO.phoneNumber.number")
    @Mapping(target = "phoneCode", source = "targetUnitAccountContactDTO.phoneNumber.countryCode")
    TargetUnitAccountContact toTargetUnitAccountContact(TargetUnitAccountContactDTO targetUnitAccountContactDTO, TargetUnitAccountContactType contactType);

    @Mapping(target = "phoneNumber.number", source = "phoneNumber")
    @Mapping(target = "phoneNumber.countryCode", source = "phoneCode")
    TargetUnitAccountContactDTO toTargetUnitAccountContactDTO(TargetUnitAccountContact targetUnitAccountContact);

    @Named("responsiblePersonToDTO")
    default TargetUnitAccountContactDTO responsiblePersonToTargetUnitAccountContactDTO(List<TargetUnitAccountContact> targetUnitAccountContacts) {
        return targetUnitAccountContacts.stream()
                .filter(contact -> contact.getContactType().equals(TargetUnitAccountContactType.RESPONSIBLE_PERSON))
                .findFirst().map(this::toTargetUnitAccountContactDTO).orElse(new TargetUnitAccountContactDTO());
    }

    @Named("administrativeContactDetailsToDTO")
    default TargetUnitAccountContactDTO administrativeContactDetailsToTargetUnitAccountContactDTO(List<TargetUnitAccountContact> targetUnitAccountContacts) {
        return targetUnitAccountContacts.stream()
                .filter(contact -> contact.getContactType().equals(TargetUnitAccountContactType.ADMINISTRATIVE_CONTACT_DETAILS))
                .findFirst().map(this::toTargetUnitAccountContactDTO).orElse(new TargetUnitAccountContactDTO());
    }
}
