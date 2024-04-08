package uk.gov.cca.api.authorization.regulator.transform;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionGroupLevel;
import uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static uk.gov.cca.api.authorization.core.domain.Permission.PERM_CA_USERS_EDIT;
import static uk.gov.cca.api.authorization.core.domain.Permission.PERM_TASK_ASSIGNMENT;
import static uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionGroup.ASSIGN_REASSIGN_TASKS;
import static uk.gov.cca.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;

@UtilityClass
public class RegulatorPermissionsAdapter {

    private final Map<RegulatorPermissionGroupLevel, List<String>> permissionGroupLevelsConfig;

    static {
        permissionGroupLevelsConfig = new LinkedHashMap<>();

        //MANAGE_USERS_AND_CONTACTS
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.NONE),
                Collections.emptyList());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, RegulatorPermissionLevel.EXECUTE),
                List.of(PERM_CA_USERS_EDIT));

        //ASSIGN_REASSIGN TASKS
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, RegulatorPermissionLevel.NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, RegulatorPermissionLevel.EXECUTE),
                List.of(PERM_TASK_ASSIGNMENT));
    }

    public List<String> getPermissionsFromPermissionGroupLevels(
        Map<String, RegulatorPermissionLevel> permissionGroupLevels) {
        List<String> permissions = new ArrayList<>();

        permissionGroupLevels.forEach((group, level) ->
            Optional.ofNullable(permissionGroupLevelsConfig.get(new RegulatorPermissionGroupLevel(group, level)))
                .ifPresent(permissions::addAll));

        return permissions;
    }

    public Map<String, RegulatorPermissionLevel> getPermissionGroupLevelsFromPermissions(
        List<String> permissions) {

        Map<String, RegulatorPermissionLevel> permissionGroupLevels = new LinkedHashMap<>();
        permissionGroupLevelsConfig.forEach((configGroupLevel, configPermissionList) -> {
            if (permissions.containsAll(configPermissionList) &&
                isExistingLevelLessThanConfigLevel(permissionGroupLevels.get(configGroupLevel.getGroup()),
                    configGroupLevel)) {
                permissionGroupLevels.put(configGroupLevel.getGroup(), configGroupLevel.getLevel());
            }
        });

        return permissionGroupLevels;
    }

    public Map<String, List<RegulatorPermissionLevel>> getPermissionGroupLevels() {
        return
            permissionGroupLevelsConfig.keySet().stream()
                .collect(Collectors.groupingBy(
                    RegulatorPermissionGroupLevel::getGroup,
                    LinkedHashMap::new,
                    Collectors.mapping(RegulatorPermissionGroupLevel::getLevel, toList())));
    }

    private boolean isExistingLevelLessThanConfigLevel(
        RegulatorPermissionLevel existingLevel, RegulatorPermissionGroupLevel configGroupLevel) {
        if (existingLevel == null) {
            return true;
        }
        return existingLevel.isLessThan(configGroupLevel.getLevel());
    }

}
