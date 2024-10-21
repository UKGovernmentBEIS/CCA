package uk.gov.cca.api.migration.account.identifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class AccountTargetUnitIdentifierMigrationService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final AccountIdentifierCreationService accountIdentifierService;

    private static final String MAX_BUSINESS_ID_PER_SECTOR_ACRONYM_STMT = 
            "select sec.[sect_id] as acronym, MAX(CAST(SUBSTRING ([tu_id] , CHARINDEX( '/T', [tu_id] )+2, LEN([tu_id])) AS INT)) max_business_id\r\n"
            + "from tbl_target_units tu\r\n" 
            + "join [tbl_sectors] sec on sec.sect_pk = tu.sect_id\r\n"
            + "group by sec.[sect_id]";

    @Override
    public List<String> migrate(String ids) {
        Map<String, Long> maxBusinessIdPerSectorAcronym = migrationJdbcTemplate.query(MAX_BUSINESS_ID_PER_SECTOR_ACRONYM_STMT, new ResultSetExtractor<Map<String,Long>>() {
            @Override
            public Map<String,Long> extractData(ResultSet rs) throws SQLException {
                HashMap<String, Long> map = new HashMap<>();
                while(rs.next()){
                    map.put(rs.getString("acronym"), rs.getLong("max_business_id"));
                }
                return map;
            }
        });

        if (!MapUtils.isEmpty(maxBusinessIdPerSectorAcronym)) {
            accountIdentifierService.updateTargetUnitAccountIdentifiers(maxBusinessIdPerSectorAcronym);
        }

        return List.of();
    }

    @Override
    public String getResource() {
        return "account-target-unit-ids";
    }

}
