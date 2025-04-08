package uk.gov.cca.api.migration.underlyingagreement.baselinetargets;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationContainer;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationUtil;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementSectionMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachment;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetPeriod5DetailsSectionMigrationService  implements UnderlyingAgreementSectionMigrationService<TargetPeriod5Details> {
            
    private final JdbcTemplate migrationJdbcTemplate;
    private final TargetPeriod6DetailsAttachmentsBuilder targetPeriod6DetailsAttachmentsBuilder;
    private final TargetPeriod6DetailsBuilder targetPeriod6DetailsBuilder;
    
    private static final String TP5_DETAILS_STMT =
            " SELECT [bydata_pk], \r\n"
            + "        tu.[target_unit_pk], \r\n"
            + "        tu.[tu_id], \r\n"
            + "        [last_updated], \r\n"
            + "        [tp5_target_type] as tp_target_type, \r\n"
            + "        [tp5_srm] as tp_srm, \r\n"
            + "        [tp5_energy_carbon_unit] as tp_energy_carbon_unit, \r\n"
            + "        [tp5_throughput_unit] as tp_throughput_unit, \r\n"
            + "        [tp5_estimated_data] as tp_estimated_data, \r\n"
            + "        [tp5_by_start] as tp_by_start, \r\n"
            + "        [tp5_by_energy_carbon] as tp_by_energy_carbon, \r\n"
            + "        [tp5_by_throughput] as tp_by_throughput, \r\n"
            + "        [tp5_by_cef] as tp_by_cef, \r\n"
            + "        [tp5_target_value] as tp_target_value, \r\n"
            + "        [tp5_target_percent] as tp_target_percent, \r\n"
            + "        [tp5_target_tolerance_percent] as tp_target_tolerance_percent, \r\n"
            + "        tu.[subsector_name], \r\n"
            + "        case when tu.subsector_name IS NULL then sec.[sect_target_type] else subsec.[target_type] end as sect_target_type, \r\n"
            + "        case when tu.subsector_name IS NULL then sec.[energy_carbon_units] else subsec.[energy_carbon_unit] end as sect_energy_carbon_unit, \r\n"
            + "        case when tu.subsector_name IS NULL then sec.[throughput_units] else subsec.[throughput_unit] end as sect_throughput_unit \r\n"
            + "        FROM [tbl_by_data] tbd \r\n"
            + "        JOIN [tbl_target_units] tu on tu.target_unit_pk = tbd.tu_id \r\n"
            + "        JOIN [tbl_sectors] sec on sec.sect_pk = tu.sect_id \r\n"
            + "        LEFT JOIN [tbl_sub_sectors] subsec on subsec.sub_sector_name = tu.subsector_name \r\n"
            + "        WHERE tu.tu_status = 2 AND tu.currently_in_cca = 1";
    
    
    
    public TargetPeriod5DetailsSectionMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
			TargetPeriod6DetailsAttachmentsBuilder targetPeriod6DetailsAttachmentsBuilder,
			TargetPeriod6DetailsBuilder targetPeriod6DetailsBuilder) {
		super();
		this.migrationJdbcTemplate = migrationJdbcTemplate;
		this.targetPeriod6DetailsAttachmentsBuilder = targetPeriod6DetailsAttachmentsBuilder;
		this.targetPeriod6DetailsBuilder = targetPeriod6DetailsBuilder;
	}

	public void populateSection(List<String> eligibleTargetUnitIds, Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap) {
        Map<String, TargetPeriod5Details> sections = querySection(eligibleTargetUnitIds);
        final boolean createCopy = true;
        
        sections.forEach((targetUnitId, section) -> {
            
            if (Boolean.TRUE.equals(section.getExist())) {
                List<LegacyFileAttachment> sectionAttachments = new ArrayList<>();
                
                targetPeriod6DetailsAttachmentsBuilder.populateAttachments(targetUnitId, section.getDetails(), createCopy, sectionAttachments);
                
                migrationContainerMap.get(targetUnitId).getUnderlyingAgreementContainer().getUnderlyingAgreementAttachments()
                    .putAll(sectionAttachments.stream().collect(toMap(file -> UUID.fromString(file.getFileAttachment().getUuid()), file -> file.getFileAttachment().getFileName())));
                migrationContainerMap.get(targetUnitId).getFileAttachments().addAll(sectionAttachments);
            }
            
            migrationContainerMap.get(targetUnitId).getUnderlyingAgreementContainer().getUnderlyingAgreement().setTargetPeriod5Details(section);
        });
    }
    
    public Map<String, TargetPeriod5Details> querySection(List<String> eligibleTargetUnitIds) {
        Map<String, TargetPeriod5Details> sectionPerTargetUnit = new HashMap<>();
        
        String query = UnderlyingAgreementMigrationUtil.constructSectionQuery(TP5_DETAILS_STMT, eligibleTargetUnitIds);
        Map<String, TargetPeriod6DetailsVO> targetPeriod6DetailsVOsPerAccount = executeQuery(query, eligibleTargetUnitIds);
        
        for (Entry<String, TargetPeriod6DetailsVO> entry : targetPeriod6DetailsVOsPerAccount.entrySet()) {
            String targetUnitId = entry.getKey();
            TargetPeriod6DetailsVO targetPeriod6DetailsVO = entry.getValue();
            targetPeriod6DetailsVO.setTP6(false);
            
            TargetPeriod5Details section = new TargetPeriod5Details();
            boolean sectionExists = targetPeriod6DetailsVO.getAgreementCompositionType() != null;
            section.setExist(sectionExists);
            if(sectionExists) {
                section.setDetails(targetPeriod6DetailsBuilder.constructTargetPeriod6Details(targetPeriod6DetailsVO));
            }
            
            sectionPerTargetUnit.put(targetUnitId, section);
        }

        return sectionPerTargetUnit;
    }
        
    private Map<String, TargetPeriod6DetailsVO> executeQuery(String query, List<String> eligibleTargetUnitIds) {
        List<TargetPeriod6DetailsVO> targetPeriod6DetailsVOs = migrationJdbcTemplate.query(query,
                new TargetPeriod6DetailsRowMapper(), eligibleTargetUnitIds.isEmpty() ? new Object[] {} : eligibleTargetUnitIds.stream().map(MigrationUtil::convertCcaToLegacyBusinessId).toArray());
        return targetPeriod6DetailsVOs
                .stream()
                .collect(Collectors.toMap(targetPeriod6DetailsVO -> MigrationUtil.convertLegacyToCcaBusinessId(targetPeriod6DetailsVO.getTargetUnitId()),
                        targetPeriod6DetailsVO -> targetPeriod6DetailsVO));
    }

}
