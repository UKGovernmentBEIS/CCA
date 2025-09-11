package uk.gov.cca.api.migration.account.siccodes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import lombok.AllArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.CompanyProfileInfo;
import uk.gov.cca.api.account.domain.dto.UpdateTargetUnitAccountSicCodeDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.netz.api.companieshouse.CompanyInformationService;
import uk.gov.netz.api.companieshouse.SicCode;

@Service
@AllArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetUnitAccountSicCodesService {
	
	private final TargetUnitAccountRepository targetUnitAccountRepository;
	private final CompanyInformationService companyInformationService;
	private final TargetUnitAccountUpdateService targetUnitAccountUpdateService;
	
	@Transactional
	public Map<Long, String> findTargetUnitAccountsWithCrnWithoutSicCodes() {
		return targetUnitAccountRepository.findTargetUnitAccountsWithCrnAndStatusIn(
				List.of(TargetUnitAccountStatus.NEW, TargetUnitAccountStatus.LIVE)).stream()
				.filter(account -> ObjectUtils.isEmpty(account.getSicCodes()))
				.collect(Collectors.toMap(TargetUnitAccount::getId, TargetUnitAccount::getCompanyRegistrationNumber));
	}

	@Transactional
	public void updateSicCodes(Long accountId, String crn, List<String> results) {
		CompanyProfileInfo company = companyInformationService.getCompanyProfile(crn, CompanyProfileInfo.class);
		if (ObjectUtils.isEmpty(company)) {
			results.add("CRN: " + crn + " | CRN was not found on Companies House");
		} else if (ObjectUtils.isEmpty(company.getSicCodes())) {
			results.add("CRN: " + crn + " | No SIC codes found on Companies House");
		} else {
			targetUnitAccountUpdateService.updateTargetUnitAccountSicCodes(
					accountId, UpdateTargetUnitAccountSicCodeDTO.builder()
					.sicCodes(company.getSicCodes().stream().map(SicCode::getCode).toList())
					.build());
			
			results.add("CRN: " + crn + " | Success");
		}
	}
}
