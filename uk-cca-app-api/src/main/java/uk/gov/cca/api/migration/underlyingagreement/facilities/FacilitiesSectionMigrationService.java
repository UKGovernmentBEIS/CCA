package uk.gov.cca.api.migration.underlyingagreement.facilities;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementFacilitiesSectionMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationContainer;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationUtil;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachment;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class FacilitiesSectionMigrationService implements UnderlyingAgreementFacilitiesSectionMigrationService<Facility>{
    
    private final JdbcTemplate migrationJdbcTemplate;
    private final FacilityBuilder facilityBuilder;
    private final FacilitiesAttachmentsBuilder facilitiesAttachmentsBuilder;
    
    List<Pair<Long, String>> legacyCountries = new ArrayList<>();

    private static final String LIVE_FACILITIES_STMT = 
            "SELECT tf.[facility_pk] \r\n"
            + "      ,tf.[facility_id] \r\n"
            + "      ,tu.[target_unit_pk] \r\n"
            + "      ,tu.[tu_id] \r\n"
            + "      ,tf.[facility_name] \r\n"
            + "      ,tf.[facility_add_line1] \r\n"
            + "      ,tf.[facility_add_line2] \r\n"
            + "      ,tf.[facility_add_city] \r\n"
            + "      ,tf.[facility_add_county] \r\n"
            + "      ,tf.[facility_add_postcode] \r\n"
            + "      ,tf.[facility_add_country] \r\n"
            + "      ,tf.[fef_application_reason] \r\n"
            + "      ,tf.[installation_energy] \r\n"
            + "      ,tf.[uk_ets_ids] \r\n"
            + "      ,ptf.facility_id as previous_fac_id \r\n"
            + "      ,tf.[connected_facility] \r\n"
            + "      ,tf.[eligible_facility_energy] \r\n"
            + "      ,tf.[facility_exit_date] \r\n"
            + "      ,tf.[facility_exited_for_previous_tP] \r\n"
            + "      ,tf.[facility_entry_date] \r\n"
            + "      ,tf.[3_ 7ths_provision] \r\n"
            + "      ,tf.[agreement_type] \r\n"
            + "      ,tf.[contact_email] \r\n"
            + "      ,tf.[contact_title] \r\n"
            + "      ,tf.[contact_organisation] \r\n"
            + "      ,tf.[contact_telephone] \r\n"
            + "      ,tf.[contact_forename] \r\n"
            + "      ,tf.[contact_surname] \r\n"
            + "      ,tf.[contact_role] \r\n"
            + "      ,tf.[contact_add_line1] \r\n"
            + "      ,tf.[contact_add_line2] \r\n"
            + "      ,tf.[contact_add_city] \r\n"
            + "      ,tf.[contact_add_county] \r\n"
            + "      ,tf.[contact_add_postcode] \r\n"
            + "      ,tf.[contact_add_country] \r\n"
            + "      ,tf.[facility_status] \r\n"
            + "      ,tf.[epr_held] \r\n"
            + "      ,tf.[epr_authorisations] \r\n"
            + "      ,tf.[epr_regulator] \r\n"
            + "  FROM [tbl_facility] tf \r\n"
            + "  inner join [tbl_target_units] tu on tf.tu_id = tu.target_unit_pk \r\n"
            + "  left join [tbl_facility] ptf on ptf.facility_pk = tf.previous_facility_id \r\n"
            + "  WHERE tf.[facility_status] = 2  \r\n"
            + "  AND tf.[facility_currently_in_cca] = 1 ";
    
    private static final String COUNTRIES_STMT = 
            "SELECT \r\n"
            + "  [country_id] \r\n"
            + " ,[country] \r\n"
            + "FROM [tbl_country] \r\n"
            + "WHERE 1 = 1";    
    
    
    
    public FacilitiesSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate, FacilityBuilder facilityBuilder,
			FacilitiesAttachmentsBuilder facilitiesAttachmentsBuilder, List<Pair<Long, String>> legacyCountries) {
		super();
		this.migrationJdbcTemplate = migrationJdbcTemplate;
		this.facilityBuilder = facilityBuilder;
		this.facilitiesAttachmentsBuilder = facilitiesAttachmentsBuilder;
		this.legacyCountries = legacyCountries;
	}

	public void populateSection(List<String> eligibleTargetUnitIds, Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap) {
        Map<String, Set<Facility>> sections = querySection(eligibleTargetUnitIds);

        sections.forEach((targetUnitId, section) -> {
            
            List<LegacyFileAttachment> sectionAttachments = new ArrayList<>();
            
            section.forEach(facility ->
                    facilitiesAttachmentsBuilder.populateAttachments(sectionAttachments, facility));
            
            migrationContainerMap.get(targetUnitId).getUnderlyingAgreementContainer().getUnderlyingAgreement().setFacilities(section);
            migrationContainerMap.get(targetUnitId).getUnderlyingAgreementContainer().getUnderlyingAgreementAttachments()
                .putAll(sectionAttachments.stream().collect(toMap(file -> UUID.fromString(file.getFileAttachment().getUuid()), file -> file.getFileAttachment().getFileName())));
            migrationContainerMap.get(targetUnitId).getFileAttachments().addAll(sectionAttachments);
        });
    }    
    
    public Map<String, Set<Facility>> querySection(List<String> eligibleTargetUnitIds) {
        Map<String, Set<Facility>> sectionPerTargetUnit = new HashMap<>();
        
        String query = UnderlyingAgreementMigrationUtil.constructSectionQuery(LIVE_FACILITIES_STMT, eligibleTargetUnitIds);
        Map<String, List<FacilityItemVO>> facilityItemVOsPerAccount = executeQuery(query, eligibleTargetUnitIds);
        
        legacyCountries = getLegacyCountries();
        
        for (Entry<String, List<FacilityItemVO>> entry : facilityItemVOsPerAccount.entrySet()) {
            String targetUnitId = entry.getKey();
            List<FacilityItemVO> facilityItemVOs = entry.getValue();
            
            Set<Facility> section = new HashSet<>();
            facilityItemVOs.forEach(facility -> section.add(facilityBuilder.constructFacility(facility, legacyCountries)));
            sectionPerTargetUnit.put(targetUnitId, section);
        }

        return sectionPerTargetUnit;
    }
        
    private Map<String, List<FacilityItemVO>> executeQuery(String query, List<String> eligibleTargetUnitIds) {
        List<FacilityItemVO> facilityVOs = migrationJdbcTemplate.query(query,
                new FacilityItemRowMapper(), eligibleTargetUnitIds.isEmpty() ? new Object[] {} : eligibleTargetUnitIds.stream().map(MigrationUtil::convertCcaToLegacyBusinessId).toArray());
        return facilityVOs.stream()
                .collect(Collectors.groupingBy(fi -> MigrationUtil.convertLegacyToCcaBusinessId(fi.getBusinessId())));
    }
    
    private List<Pair<Long, String>> getLegacyCountries() {
        return migrationJdbcTemplate.query(COUNTRIES_STMT,
                (rs, rowNum) -> Pair.of(rs.getLong("country_id"), rs.getString("country")));
    }
}
