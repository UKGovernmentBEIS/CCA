package uk.gov.cca.api.user.operator.transform;

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
import org.mapstruct.ReportingPolicy;
import org.springframework.util.ObjectUtils;

import jakarta.validation.Valid;
import uk.gov.cca.api.user.operator.domain.CcaOperatorUserRegistrationWithCredentialsDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.user.core.domain.enumeration.KeycloakUserAttributes;

@Mapper(componentModel = "spring", config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface CcaOperatorUserRegistrationMapper {

	@Mapping(source = "email", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userId", target = "id")
    UserRepresentation toUserRepresentation(@Valid CcaOperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationDTO, String email, String userId);

    @AfterMapping
    default void populateAttributesToUserRepresentation(
    	CcaOperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationDTO, @MappingTarget UserRepresentation userRepresentation) {
        
    	populateUserRepresentationAttributes(operatorUserRegistrationDTO, userRepresentation);

        /* Set credentials */
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(operatorUserRegistrationDTO.getPassword());
        userRepresentation.setCredentials(Collections.singletonList(credentials));
    } 

    private void populateUserRepresentationAttributes(CcaOperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationDTO,
                                                      UserRepresentation userRepresentation) {
        Map<String, List<String>> attributes = new HashMap<>();

        attributes.put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
                (Objects.isNull(operatorUserRegistrationDTO.getPhoneNumber()) ||
                    ObjectUtils.isEmpty(operatorUserRegistrationDTO.getPhoneNumber().getCountryCode())) ? null
                    : Collections.singletonList(operatorUserRegistrationDTO.getPhoneNumber().getCountryCode()));
            attributes.put(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                (Objects.isNull(operatorUserRegistrationDTO.getPhoneNumber()) ||
                    ObjectUtils.isEmpty(operatorUserRegistrationDTO.getPhoneNumber().getNumber())) ? null
                    : Collections.singletonList(operatorUserRegistrationDTO.getPhoneNumber().getNumber()));
            attributes.put(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
                (Objects.isNull(operatorUserRegistrationDTO.getMobileNumber()) ||
                    ObjectUtils.isEmpty(operatorUserRegistrationDTO.getMobileNumber().getCountryCode())) ? null
                    : Collections.singletonList(operatorUserRegistrationDTO.getMobileNumber().getCountryCode()));
            attributes.put(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                (Objects.isNull(operatorUserRegistrationDTO.getMobileNumber()) ||
                    ObjectUtils.isEmpty(operatorUserRegistrationDTO.getMobileNumber().getNumber())) ? null
                    : Collections.singletonList(operatorUserRegistrationDTO.getMobileNumber().getNumber()));
            attributes.put(KeycloakUserAttributes.JOB_TITLE.getName(),
                    ObjectUtils.isEmpty(operatorUserRegistrationDTO.getJobTitle()) ? null
                            : Collections.singletonList(String.valueOf(operatorUserRegistrationDTO.getJobTitle())));
            userRepresentation.setAttributes(attributes);
    }
}
