package uk.gov.cca.api.migration.underlyingagreement.facilityidentifier;

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
public class FacilityIdentificationMigrationService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final FacilityIdentificationService facilityIdentificationService;

    private static final String MAX_BUSINESS_ID_PER_SECTOR_ACRONYM_STMT = 
            "select sec.[sect_id] as acronym, MAX(CAST(SUBSTRING ([facility_id] , CHARINDEX( '/F', [facility_id] )+2, LEN([facility_id])) AS INT)) max_business_id \r\n"
            + "from tbl_facility tf \r\n" 
            + "join [tbl_target_units] tu on tf.tu_id = tu.target_unit_pk \r\n"
            + "join [tbl_sectors] sec on sec.sect_pk = tu.sect_id \r\n"
            + "group by sec.[sect_id]";
    

    public FacilityIdentificationMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
			FacilityIdentificationService facilityIdentificationService) {
		super();
		this.migrationJdbcTemplate = migrationJdbcTemplate;
		this.facilityIdentificationService = facilityIdentificationService;
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
            facilityIdentificationService.updateFacilityIdentifiers(maxBusinessIdPerSector);
        }

        return List.of("Done");
    }

    @Override
    public String getResource() {
        return "facility-identifiers";
    }

}
