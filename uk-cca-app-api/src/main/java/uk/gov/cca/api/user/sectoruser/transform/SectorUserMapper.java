package uk.gov.cca.api.user.sectoruser.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.*;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserAuthorityDetailsDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.user.core.domain.enumeration.KeycloakUserAttributes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

/**
 * The Sector User Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectorUserMapper {

    @Mapping(target = "username", source = "email")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserRepresentation toUserRepresentation(String email, String firstName, String lastName);

    @Mapping(target = "email", source = "userRepresentation.username")
    @Mapping(target = "contactType", source = "authorityDetails.contactType")
    @Mapping(target = "organisationName", source = "authorityDetails.organisationName")
    SectorUserAuthorityDetailsDTO toSectorUserDTO(UserRepresentation userRepresentation, CcaAuthorityDetails authorityDetails);

    @Mapping(target = "email", source = "username")
    SectorUserDTO toSectorUserDTO(UserRepresentation userRepresentation);

    @AfterMapping
    default void populateAttributeToSectorUserDTO(UserRepresentation userRepresentation, @MappingTarget SectorUserDTO sectorUserDTO) {
        if (ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()))
                .ifPresent(list -> sectorUserDTO.setJobTitle(ObjectUtils.isEmpty(list) ? null : list.get(0)));


        /* Set phone number */
        PhoneNumberDTO phoneNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()))
                .ifPresent(list -> phoneNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> phoneNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        sectorUserDTO.setPhoneNumber(phoneNumber);

        /* Set Mobile number */
        PhoneNumberDTO mobileNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()))
                .ifPresent(list -> mobileNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> mobileNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        sectorUserDTO.setMobileNumber(mobileNumber);
    }

    @Mapping(target = "username", source = "sectorUserDTO.email")
    @Mapping(target = "firstName", source = "sectorUserDTO.firstName")
    @Mapping(target = "lastName", source = "sectorUserDTO.lastName")
    @Mapping(target = "email", source = "sectorUserDTO.email")
    @Mapping(target = "enabled", ignore = true)
    UserRepresentation toUserRepresentation(SectorUserDTO sectorUserDTO);

    @AfterMapping
    default void populateAttributesToUserRepresentation(SectorUserDTO sectorUserDTO, @MappingTarget UserRepresentation userRepresentation) {

        // set jon title
        userRepresentation.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(), sectorUserDTO.getJobTitle());

        // Set phone numbers
	    Optional.ofNullable(sectorUserDTO.getPhoneNumber()).ifPresentOrElse(phoneNumber -> {
		    userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
				    phoneNumber.getCountryCode());
		    userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
				    phoneNumber.getNumber());
	    }, () -> {
		    userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(), null);
		    userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(), null);
	    });

        Optional.ofNullable(sectorUserDTO.getMobileNumber()).ifPresentOrElse(mobileNumber -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
                    mobileNumber.getCountryCode());
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                    mobileNumber.getNumber());
        }, () -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), null);
        });
    }
}
