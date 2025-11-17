package uk.gov.cca.api.authorization.ccaauth.regulator.transform;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_ADMIN_TERMINATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_ADMIN_TERMINATION_SUBMISSION;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_FACILITY_AUDIT_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_FACILITY_AUDIT_SUBMISSION;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_OPERATOR_USERS_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_SECTOR_ASSOCIATION_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_SECTOR_USERS_EDIT;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission.PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.ADMIN_TERMINATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.ADMIN_TERMINATION_SUBMISSION;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.FACILITY_AUDIT_SUBMISSION;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.MANAGE_FACILITY_AUDIT;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.MANAGE_OPERATOR_USERS;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.MANAGE_SECTOR_ASSOCIATIONS;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.MANAGE_SECTOR_USERS;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_APPLICATION_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW;
import static uk.gov.cca.api.authorization.ccaauth.regulator.domain.CcaRegulatorPermissionGroup.UNDERLYING_AGREEMENT_VARIATION_REVIEW;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_ACCOUNT_USERS_EDIT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_CA_USERS_EDIT;
import static uk.gov.netz.api.authorization.core.domain.Permission.PERM_TASK_ASSIGNMENT;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.ASSIGN_REASSIGN_TASKS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.netz.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class CcaRegulatorPermissionsAdapterTest {
    private CcaRegulatorPermissionsAdapter regulatorPermissionsAdapter;

    @BeforeAll
    void beforeAll() {
        regulatorPermissionsAdapter = new CcaRegulatorPermissionsAdapter();
        regulatorPermissionsAdapter.afterPropertiesSet();
    }

    @Test
    void getPermissionsFromPermissionGroupLevels_one_permission_per_group_level() {
        Map<String, RegulatorPermissionLevel> permissionGroupLevels =
                Map.ofEntries(Map.entry(MANAGE_USERS_AND_CONTACTS, NONE),
                        Map.entry(ASSIGN_REASSIGN_TASKS, EXECUTE),
                        Map.entry(MANAGE_SECTOR_ASSOCIATIONS, EXECUTE),
                        Map.entry(MANAGE_SECTOR_USERS, EXECUTE),
                        Map.entry(MANAGE_OPERATOR_USERS, EXECUTE),
                        Map.entry(ADMIN_TERMINATION_SUBMISSION, EXECUTE),
                        Map.entry(ADMIN_TERMINATION_PEER_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_VARIATION_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, EXECUTE),
                        Map.entry(FACILITY_AUDIT_SUBMISSION, EXECUTE));

        List<String> expectedPermissions = List.of(
                PERM_TASK_ASSIGNMENT, PERM_SECTOR_ASSOCIATION_EDIT, PERM_SECTOR_USERS_EDIT, PERM_OPERATOR_USERS_EDIT, PERM_ADMIN_TERMINATION_SUBMISSION, PERM_ADMIN_TERMINATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW, PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW, PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW, PERM_FACILITY_AUDIT_SUBMISSION);

        assertThat(regulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
                .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionsFromPermissionGroupLevels_multiple_permissions() {
        Map<String, RegulatorPermissionLevel> permissionGroupLevels = Map.ofEntries(
                Map.entry(MANAGE_USERS_AND_CONTACTS, EXECUTE),
                Map.entry(ASSIGN_REASSIGN_TASKS, EXECUTE),
                Map.entry(MANAGE_SECTOR_ASSOCIATIONS, NONE),
                Map.entry(MANAGE_SECTOR_USERS, NONE),
                Map.entry(MANAGE_OPERATOR_USERS, NONE),
                Map.entry(ADMIN_TERMINATION_SUBMISSION, EXECUTE),
                Map.entry(ADMIN_TERMINATION_PEER_REVIEW, EXECUTE),
                Map.entry(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, EXECUTE),
                Map.entry(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, EXECUTE),
                Map.entry(UNDERLYING_AGREEMENT_VARIATION_REVIEW, EXECUTE),
                Map.entry(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, EXECUTE),
                Map.entry(FACILITY_AUDIT_SUBMISSION, EXECUTE)
        );

        List<String> expectedPermissions = List.of(
                PERM_CA_USERS_EDIT,
                PERM_TASK_ASSIGNMENT,
                PERM_ADMIN_TERMINATION_SUBMISSION,
                PERM_ADMIN_TERMINATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW,
                PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW,
                PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW,
                PERM_FACILITY_AUDIT_SUBMISSION);

        assertThat(regulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
                .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions_one_permission_per_group_level() {
        Map<String, RegulatorPermissionLevel> permissionGroupLevels =
                Map.ofEntries(Map.entry(MANAGE_USERS_AND_CONTACTS, NONE),
                        Map.entry(ASSIGN_REASSIGN_TASKS, EXECUTE),
                        Map.entry(MANAGE_SECTOR_ASSOCIATIONS, NONE),
                        Map.entry(MANAGE_SECTOR_USERS, NONE),
                        Map.entry(MANAGE_OPERATOR_USERS, NONE),
                        Map.entry(ADMIN_TERMINATION_SUBMISSION, EXECUTE),
                        Map.entry(ADMIN_TERMINATION_PEER_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_VARIATION_REVIEW, EXECUTE),
                        Map.entry(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, EXECUTE),
                        Map.entry(FACILITY_AUDIT_SUBMISSION, EXECUTE)
                );

        List<String> expectedPermissions = List.of(
                PERM_TASK_ASSIGNMENT, PERM_ADMIN_TERMINATION_SUBMISSION, PERM_ADMIN_TERMINATION_PEER_REVIEW, PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW, PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW, PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW, PERM_FACILITY_AUDIT_SUBMISSION);

        assertThat(regulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(permissionGroupLevels))
                .containsExactlyInAnyOrderElementsOf(expectedPermissions);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions_multiple_permissions_per_group_level() {
        List<String> permissions = List.of(
                PERM_ACCOUNT_USERS_EDIT,
                PERM_TASK_ASSIGNMENT,
                PERM_ADMIN_TERMINATION_SUBMISSION,
                PERM_ADMIN_TERMINATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW,
                PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW,
                PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW,
                PERM_FACILITY_AUDIT_SUBMISSION);

        Map<String, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, NONE);
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_SECTOR_ASSOCIATIONS, NONE);
        expectedPermissionGroupLevels.put(MANAGE_SECTOR_USERS, NONE);
        expectedPermissionGroupLevels.put(MANAGE_OPERATOR_USERS, NONE);
        expectedPermissionGroupLevels.put(ADMIN_TERMINATION_SUBMISSION, EXECUTE);
        expectedPermissionGroupLevels.put(ADMIN_TERMINATION_PEER_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_VARIATION_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(FACILITY_AUDIT_SUBMISSION, EXECUTE);
		expectedPermissionGroupLevels.put(MANAGE_FACILITY_AUDIT,  NONE);


        assertThat(regulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
                .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevelsFromPermissions() {
        List<String> permissions = List.of(
                PERM_ACCOUNT_USERS_EDIT,
                PERM_TASK_ASSIGNMENT,
                PERM_CA_USERS_EDIT,
                PERM_SECTOR_ASSOCIATION_EDIT,
                PERM_SECTOR_USERS_EDIT,
                PERM_OPERATOR_USERS_EDIT,
                PERM_ADMIN_TERMINATION_SUBMISSION,
                PERM_ADMIN_TERMINATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW,
                PERM_UNDERLYING_AGREEMENT_APPLICATION_REVIEW,
                PERM_UNDERLYING_AGREEMENT_VARIATION_REVIEW,
                PERM_UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW,
                PERM_FACILITY_AUDIT_SUBMISSION,
		        PERM_FACILITY_AUDIT_EDIT
        );

        Map<String, RegulatorPermissionLevel> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, EXECUTE);
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_SECTOR_ASSOCIATIONS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_SECTOR_USERS, EXECUTE);
        expectedPermissionGroupLevels.put(MANAGE_OPERATOR_USERS, EXECUTE);
        expectedPermissionGroupLevels.put(ADMIN_TERMINATION_SUBMISSION, EXECUTE);
        expectedPermissionGroupLevels.put(ADMIN_TERMINATION_PEER_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_VARIATION_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, EXECUTE);
        expectedPermissionGroupLevels.put(FACILITY_AUDIT_SUBMISSION, EXECUTE);
	    expectedPermissionGroupLevels.put(MANAGE_FACILITY_AUDIT,  EXECUTE);

        assertThat(regulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(permissions))
                .containsExactlyInAnyOrderEntriesOf(expectedPermissionGroupLevels);
    }

    @Test
    void getPermissionGroupLevels() {
        Map<String, List<RegulatorPermissionLevel>> expectedPermissionGroupLevels = new LinkedHashMap<>();
        expectedPermissionGroupLevels.put(MANAGE_USERS_AND_CONTACTS, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(ASSIGN_REASSIGN_TASKS, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_SECTOR_ASSOCIATIONS, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_SECTOR_USERS, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_OPERATOR_USERS, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(ADMIN_TERMINATION_SUBMISSION, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(ADMIN_TERMINATION_PEER_REVIEW, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_APPLICATION_REVIEW, List.of(EXECUTE, NONE));
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_VARIATION_REVIEW, List.of(EXECUTE, NONE));
        expectedPermissionGroupLevels.put(UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(FACILITY_AUDIT_SUBMISSION, List.of(NONE, EXECUTE));
        expectedPermissionGroupLevels.put(MANAGE_FACILITY_AUDIT, List.of(NONE, EXECUTE));


        Map<String, List<RegulatorPermissionLevel>> actualPermissionGroupLevels =
                regulatorPermissionsAdapter.getPermissionGroupLevels();

        assertThat(actualPermissionGroupLevels.keySet())
                .containsExactlyInAnyOrderElementsOf(expectedPermissionGroupLevels.keySet());
        actualPermissionGroupLevels.forEach((group, levels) ->
                assertThat(levels).containsExactlyElementsOf(expectedPermissionGroupLevels.get(group)));
    }
}
