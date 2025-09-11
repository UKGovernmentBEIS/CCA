package uk.gov.cca.api.migration.account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDTO;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.repository.CountryRepository;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetUnitAccountDTOBuilder {
    
    private static final String REGISTRATION_NUMBER_MISSING_REASON = "Sector to confirm why company doesn't have a company registration number.";
    private final CountryRepository countryRepository;
    
    public TargetUnitAccountDTO constructTargetUnitAccountDTO (TargetUnitAccountVO targetUnitVO, Long sectorId, Long subSectorId, List<Pair<Long, String>> legacyCountries) {
        AccountAddressDTO operatorAddressDTO = AccountAddressDTO.builder()
                .line1(targetUnitVO.getAddressLine1())
                .line2(targetUnitVO.getAddressLine2())
                .city(StringUtils.defaultIfBlank(targetUnitVO.getCity(),"-"))
                .county(targetUnitVO.getCounty())
                .postcode(targetUnitVO.getPostcode())
                .country(resolveCountryCode(targetUnitVO.getCountry(), legacyCountries))
                .build();
        
        return TargetUnitAccountDTO.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .name(targetUnitVO.getOperatorName())
                .businessId(MigrationUtil.convertLegacyToCcaBusinessId(targetUnitVO.getTargetUnitId()))
                .operatorType(getTargetUnitAccountOperatorType(targetUnitVO.getOperatorType()))
                .isCompanyRegistrationNumber(StringUtils.isNotBlank(targetUnitVO.getCompanyRegistrationNumber()))
                .companyRegistrationNumber(targetUnitVO.getCompanyRegistrationNumber())
                .registrationNumberMissingReason(StringUtils.isNotBlank(targetUnitVO.getCompanyRegistrationNumber()) ? null : REGISTRATION_NUMBER_MISSING_REASON)
                .financialIndependenceStatus(Boolean.TRUE.equals(targetUnitVO.getFinanciallyIndependent()) ? FinancialIndependenceStatus.FINANCIALLY_INDEPENDENT : FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .sicCodes(targetUnitVO.getSicCode() != null ? new ArrayList<>(Collections.singletonList(targetUnitVO.getSicCode())): null)
                .sectorAssociationId(sectorId)
                .subsectorAssociationId(subSectorId)
                .status(TargetUnitAccountStatus.NEW)
                .address(operatorAddressDTO)
                .responsiblePerson(constructTargetUnitAccountContactDTO(targetUnitVO.getResponsiblePerson(), legacyCountries))
                .administrativeContactDetails(constructTargetUnitAccountContactDTO(targetUnitVO.getAdministrativeContact(), legacyCountries))
                .creationDate(LocalDateTime.now())
                .createdBy(MigrationConstants.MIGRATION_PROCESS_USER)
                .build();
    }
    
    public TargetUnitAccountContactDTO constructTargetUnitAccountContactDTO (TargetUnitAccountContactVO contactVO, List<Pair<Long, String>> legacyCountries) {
                
        AccountAddressDTO addressDTO = AccountAddressDTO.builder()
                .line1(contactVO.getAddressLine1())
                .line2(contactVO.getAddressLine2())
                .city(StringUtils.defaultIfBlank(contactVO.getCity(),"-"))
                .county(contactVO.getCounty())
                .postcode(contactVO.getPostcode())
                .country(resolveCountryCode(contactVO.getCountry(), legacyCountries))
                .build();
        
        PhoneNumberDTO phoneNumberDTO = PhoneNumberDTO.builder()
                .countryCode(StringUtils.isBlank(contactVO.getPhoneNumber()) ? null : "44")
                .number(contactVO.getPhoneNumber())
                .build();
        
        return TargetUnitAccountContactDTO.builder()
                .firstName(contactVO.getFirstName())
                .lastName(contactVO.getLastName())
                .email(contactVO.getEmail())
                .jobTitle(contactVO.getRole())
                .address(addressDTO)
                .phoneNumber(phoneNumberDTO)
                .build();
    }

    private String resolveCountryCode(Long legacyCountryId, List<Pair<Long, String>> legacyCountries) {
        Optional<Pair<Long, String>> legacyCountryPair = legacyCountries.stream()
                .filter(country -> country.getKey().equals(legacyCountryId))
                .findFirst();
        
        if (legacyCountryPair.isEmpty()) {
            return null;
        }

        return countryRepository.findByName(legacyCountryPair.get().getValue()).map(Country::getCode).orElse(null);
    }
    
    private TargetUnitAccountOperatorType getTargetUnitAccountOperatorType(String operatorType) {
        if (operatorType == null) {
            return TargetUnitAccountOperatorType.NONE;
        }
        return Arrays.stream(TargetUnitAccountOperatorType.values())
                .filter(type -> type.getName().equalsIgnoreCase(operatorType))
                .findFirst()
                .orElse(TargetUnitAccountOperatorType.NONE);
    }

}
