package uk.gov.cca.api.user.operator.transform;

import java.util.Optional;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;

import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.user.core.domain.enumeration.KeycloakUserAttributes;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CcaOperatorUserMapper {
    
    @Mapping(target = "email", source = "username")
    CcaOperatorUserDTO toCcaOperatorUserDTO(UserRepresentation userRepresentation);

    @AfterMapping
    default void populateAttributeToCcaOperatorUserDTO(UserRepresentation userRepresentation, @MappingTarget CcaOperatorUserDTO ccaOperatorUserDTO) {
        if (ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()))
                .ifPresent(list -> ccaOperatorUserDTO.setJobTitle(ObjectUtils.isEmpty(list) ? null : list.get(0)));


        /* Set phone number */
        PhoneNumberDTO phoneNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()))
                .ifPresent(list -> phoneNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> phoneNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        ccaOperatorUserDTO.setPhoneNumber(phoneNumber);

        /* Set Mobile number */
        PhoneNumberDTO mobileNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()))
                .ifPresent(list -> mobileNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> mobileNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        ccaOperatorUserDTO.setMobileNumber(mobileNumber);
    }

    @Mapping(target = "username", source = "email")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserRepresentation toUserRepresentation(String email, String firstName, String lastName);
}
