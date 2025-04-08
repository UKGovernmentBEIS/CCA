package uk.gov.cca.api.migration.account.identifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetUnitAccountIdentificationMigrationService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final TargetUnitAccountIdentificationService accountIdentifiersService;

    private static final String MAX_BUSINESS_ID_PER_SECTOR_ACRONYM_STMT = 
            "select sec.[sect_id] as acronym, MAX(CAST(SUBSTRING ([tu_id] , CHARINDEX( '/T', [tu_id] )+2, LEN([tu_id])) AS INT)) max_business_id \r\n"
            + "from tbl_target_units tu \r\n" 
            + "join [tbl_sectors] sec on sec.sect_pk = tu.sect_id \r\n"
            + "group by sec.[sect_id]";

    public TargetUnitAccountIdentificationMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
			TargetUnitAccountIdentificationService accountIdentifiersService) {
		super();
		this.migrationJdbcTemplate = migrationJdbcTemplate;
		this.accountIdentifiersService = accountIdentifiersService;
	}

	@Override
    public List<String> migrate(String ids) {
        Map<String, Long> maxBusinessIdPerSector = migrationJdbcTemplate.query(MAX_BUSINESS_ID_PER_SECTOR_ACRONYM_STMT, new ResultSetExtractor<Map<String,Long>>() {
            @Override
            public Map<String,Long> extractData(ResultSet rs) throws SQLException {
                HashMap<String, Long> map = new HashMap<>();
                while(rs.next()){
                    map.put(rs.getString("acronym"), rs.getLong("max_business_id"));
                }
                return map;
            }
        });

        if (!MapUtils.isEmpty(maxBusinessIdPerSector)) {
            accountIdentifiersService.updateTargetUnitAccountIdentifiers(maxBusinessIdPerSector);
        }

        return List.of("Done");
    }

    @Override
    public String getResource() {
        return "target-unit-account-identifiers";
    }

}
