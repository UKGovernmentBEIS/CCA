package uk.gov.cca.api.authorization.ccaauth.regulator.transform;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroupLevel;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel;
import uk.gov.netz.api.authorization.regulator.transform.AbstarctRegulatorPermissionsAdapter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_ADMIN_TERMINATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_ADMIN_TERMINATION_SUBMISSION;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_OPERATOR_USERS_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_SECTOR_ASSOCIATION_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_SECTOR_USERS_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.ADMIN_TERMINATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.ADMIN_TERMINATION_SUBMISSION;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.MANAGE_OPERATOR_USERS;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.MANAGE_SECTOR_ASSOCIATIONS;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.MANAGE_SECTOR_USERS;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_APPLICATION_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_VARIATION_REVIEW;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_CA_USERS_EDIT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_TASK_ASSIGNMENT;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.ASSIGN_REASSIGN_TASKS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;

@Component
public class CcaRegulatorPermissionsAdapter extends AbstarctRegulatorPermissionsAdapter implements InitializingBean {
    private final Map<RegulatorPermissionGroupLevel, List<String>> permissionGroupLevelsConfig = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() {
        //MANAGE_USERS_AND_CONTACTS
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, EXECUTE),
                        List.of(PERM_CA_USERS_EDIT));

        //ASSIGN_REASSIGN TASKS
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, EXECUTE),
                        List.of(PERM_TASK_ASSIGNMENT));

        //MANAGE_SECTOR_ASSOCIATIONS
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_SECTOR_ASSOCIATIONS, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_SECTOR_ASSOCIATIONS, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_SECTOR_ASSOCIATION_EDIT));

        //MANAGE_SECTOR_USERS
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_SECTOR_USERS, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_SECTOR_USERS, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_SECTOR_USERS_EDIT));

        //MANAGE_OPERATOR_USERS
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_OPERATOR_USERS, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_OPERATOR_USERS, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_OPERATOR_USERS_EDIT));

        //ADMIN_TERMINATION_SUBMISSION
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(ADMIN_TERMINATION_SUBMISSION, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(ADMIN_TERMINATION_SUBMISSION, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_ADMIN_TERMINATION_SUBMISSION));

        //ADMIN_TERMINATION_PEER_REVIEW
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(ADMIN_TERMINATION_PEER_REVIEW, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(ADMIN_TERMINATION_PEER_REVIEW, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_ADMIN_TERMINATION_PEER_REVIEW));

        //UNDERLYING_AGREEMENT_APPLICATION_REVIEW
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());

        //UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW));

        //VARIATION_TO_UNDERLYING_AGREEMENT_REVIEW
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_VARIATION_REVIEW, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_VARIATION_REVIEW, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());

        //UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, RegulatorPermissionLevel.NONE),
                        Collections.emptyList());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, RegulatorPermissionLevel.EXECUTE),
                        List.of(PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW));
    }

    @Override
    public Map<RegulatorPermissionGroupLevel, List<String>> getPermissionGroupLevelsConfig() {
        return permissionGroupLevelsConfig;
    }
}
