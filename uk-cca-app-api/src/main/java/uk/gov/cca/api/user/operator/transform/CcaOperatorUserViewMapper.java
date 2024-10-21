package uk.gov.cca.api.user.operator.transform;

import java.util.Optional;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.ObjectUtils;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDTO;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserDetailsDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.user.core.domain.enumeration.KeycloakUserAttributes;

@Mapper(componentModel = "spring", config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface CcaOperatorUserViewMapper {

    @Mapping(target = "username", source = "email")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserRepresentation toUserRepresentation(String email, String firstName, String lastName);

    @Mapping(target = "username", source = "ccaOperatorUserDto.email")
    @Mapping(target = "firstName", source = "ccaOperatorUserDto.firstName")
    @Mapping(target = "lastName", source = "ccaOperatorUserDto.lastName")
    @Mapping(target = "email", source = "ccaOperatorUserDto.email")
    @Mapping(target = "enabled", ignore = true)
    UserRepresentation toUserRepresentation(CcaOperatorUserDTO ccaOperatorUserDto);

    @Mapping(target = "email", source = "userRepresentation.username")
    @Mapping(target = "contactType", source = "authorityDetails.contactType")
    @Mapping(target = "organisationName", source = "authorityDetails.organisationName")
    CcaOperatorUserDetailsDTO toCcaOperatorUserDetailsDTO(UserRepresentation userRepresentation, CcaAuthorityDetails authorityDetails);

    @AfterMapping
    default void populateAttributeToOperatorUserDTO(UserRepresentation userRepresentation, @MappingTarget CcaOperatorUserDetailsDTO ccaOperatorUserDetailsDTO) {
        if (ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()))
                .ifPresent(list -> ccaOperatorUserDetailsDTO.setJobTitle(ObjectUtils.isEmpty(list) ? null : list.get(0)));


        /* Set phone number */
        PhoneNumberDTO phoneNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()))
                .ifPresent(list -> phoneNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> phoneNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        ccaOperatorUserDetailsDTO.setPhoneNumber(phoneNumber);

        /* Set Mobile number */
        PhoneNumberDTO mobileNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()))
                .ifPresent(list -> mobileNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> mobileNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        ccaOperatorUserDetailsDTO.setMobileNumber(mobileNumber);
    }
    

    @AfterMapping
    default void populateAttributesToUserRepresentation(CcaOperatorUserDTO ccaOperatorUserDTO, @MappingTarget UserRepresentation userRepresentation) {

        // set job title
        userRepresentation.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(), ccaOperatorUserDTO.getJobTitle());

        // Set phone numbers
        Optional.ofNullable(ccaOperatorUserDTO.getPhoneNumber()).ifPresentOrElse(phoneNumberDTO -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
                    phoneNumberDTO.getCountryCode());
            userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                    phoneNumberDTO.getNumber());
        }, () -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(), null);
        });

        Optional.ofNullable(ccaOperatorUserDTO.getMobileNumber()).ifPresentOrElse(phoneNumberDTO -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
                    phoneNumberDTO.getCountryCode());
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                    phoneNumberDTO.getNumber());
        }, () -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), null);
        });
    }
}
