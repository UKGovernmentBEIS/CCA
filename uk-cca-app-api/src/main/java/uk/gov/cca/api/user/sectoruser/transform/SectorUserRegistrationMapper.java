package uk.gov.cca.api.user.sectoruser.transform;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationDTO;
import uk.gov.cca.api.user.sectoruser.domain.SectorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.user.core.domain.enumeration.KeycloakUserAttributes;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface SectorUserRegistrationMapper {

    @Mapping(source = "email", target = "username")
    @Mapping(source = "email", target = "email")
    UserRepresentation toUserRepresentation(@Valid SectorUserRegistrationWithCredentialsDTO sectorUserRegistrationWithCredentialsDTO, String email);

    @Mapping(source = "email", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userId", target = "id")
    UserRepresentation toUserRepresentation(
        SectorUserRegistrationWithCredentialsDTO sectorUserRegistrationWithCredentialsDTO, String email, String userId);

    @Mapping(source = "email", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userId", target = "id")
    UserRepresentation toUserRepresentation(SectorUserRegistrationDTO sectorUserRegistrationDTO, String email, String userId);

    @AfterMapping
    default void populateAttributesToUserRepresentation(
        SectorUserRegistrationWithCredentialsDTO sectorUserRegistrationWithCredentialsDTO, @MappingTarget
    UserRepresentation userRepresentation) {
        populateUserRepresentationAttributes(sectorUserRegistrationWithCredentialsDTO, userRepresentation);

        /* Set credentials */
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(sectorUserRegistrationWithCredentialsDTO.getPassword());
        userRepresentation.setCredentials(List.of(credentials));
    }

    private void populateUserRepresentationAttributes(SectorUserRegistrationDTO sectorUserRegistrationDTO,
                                                      UserRepresentation userRepresentation) {
        Map<String, List<String>> attributes = new HashMap<>();

        attributes.put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
            (Objects.isNull(sectorUserRegistrationDTO.getPhoneNumber()) ||
                ObjectUtils.isEmpty(sectorUserRegistrationDTO.getPhoneNumber().getCountryCode())) ? null
                : Collections.singletonList(sectorUserRegistrationDTO.getPhoneNumber().getCountryCode()));
        attributes.put(KeycloakUserAttributes.PHONE_NUMBER.getName(),
            (Objects.isNull(sectorUserRegistrationDTO.getPhoneNumber()) ||
                ObjectUtils.isEmpty(sectorUserRegistrationDTO.getPhoneNumber().getNumber())) ? null
                : Collections.singletonList(sectorUserRegistrationDTO.getPhoneNumber().getNumber()));
        attributes.put(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
            (Objects.isNull(sectorUserRegistrationDTO.getMobileNumber()) ||
                ObjectUtils.isEmpty(sectorUserRegistrationDTO.getMobileNumber().getCountryCode())) ? null
                : Collections.singletonList(sectorUserRegistrationDTO.getMobileNumber().getCountryCode()));
        attributes.put(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
            (Objects.isNull(sectorUserRegistrationDTO.getMobileNumber()) ||
                ObjectUtils.isEmpty(sectorUserRegistrationDTO.getMobileNumber().getNumber())) ? null
                : Collections.singletonList(sectorUserRegistrationDTO.getMobileNumber().getNumber()));
        attributes.put(KeycloakUserAttributes.JOB_TITLE.getName(),
                ObjectUtils.isEmpty(sectorUserRegistrationDTO.getJobTitle()) ? null
                        : Collections.singletonList(String.valueOf(sectorUserRegistrationDTO.getJobTitle())));
        userRepresentation.setAttributes(attributes);
    }
}