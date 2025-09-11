package uk.gov.cca.api.migration.account.siccodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.netz.api.common.utils.ExceptionUtils;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetUnitAccountSicCodesMigrationService extends MigrationBaseService {

	private final TargetUnitAccountSicCodesService targetUnitAccountSicCodesService;

    public TargetUnitAccountSicCodesMigrationService(
    		TargetUnitAccountSicCodesService targetUnitAccountSicCodesService) {
		super();
		this.targetUnitAccountSicCodesService = targetUnitAccountSicCodesService;
	}

	@Override
    public List<String> migrate(String crns) {
		List<String> results = new ArrayList<>();	
        Map<Long, String> accountCrnMap = targetUnitAccountSicCodesService
        		.findTargetUnitAccountsWithCrnWithoutSicCodes();
        
        if (!MapUtils.isEmpty(accountCrnMap)) {
        	if (accountCrnMap.size() > 500) {
            	results.add("The number of affected accounts (" + accountCrnMap.size() + 
            			") exceeds the Companies House rate limiting (500)");
            } else {
            	accountCrnMap.forEach((accountId, crn) -> {
                	try {
                		targetUnitAccountSicCodesService.updateSicCodes(accountId, crn, results);
                	} catch (Exception ex) {
                		log.error("migration of SIC codes for target unit with CRN: {} failed with {}",
                				crn, ExceptionUtils.getRootCause(ex).getMessage());
                        
                        results.add("CRN: " + crn + " | Error: " + ExceptionUtils.getRootCause(ex).getMessage());
                	}
                });
            }
        }
        
        return results;
    }

    @Override
    public String getResource() {
        return "target-unit-account-sic-codes";
    }
}
