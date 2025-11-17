package uk.gov.cca.api.migration.underlyingagreement.facilities;

import static uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus.LIVE;
import static uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus.NEW;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountContactDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.MigrationUtil;
import uk.gov.cca.api.underlyingagreement.domain.facilities.AgreementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Apply70Rule;
import uk.gov.cca.api.underlyingagreement.domain.facilities.EligibilityDetailsAndAuthorisation;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityExtent;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.netz.api.common.domain.PhoneNumberDTO;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.repository.CountryRepository;
 
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class FacilityBuilder {

    private final CountryRepository countryRepository;
    
    public Facility constructFacility (FacilityItemVO facilityItemVO, List<Pair<Long, String>> legacyCountries) {
        
        FacilityItem facilityItem = constructFacilityItem(facilityItemVO, legacyCountries);
        
        return Facility.builder()
                .status(ApplicationReasonType.CHANGE_OF_OWNERSHIP.equals(facilityItem.getFacilityDetails().getApplicationReason()) ? LIVE : NEW)
                .facilityItem(facilityItem)
                .build();
    }
    
    private FacilityItem constructFacilityItem (FacilityItemVO facilityItemVO, List<Pair<Long, String>> legacyCountries) {
        ApplicationReasonType applicationReason = FacilitiesUtil.getApplicationReasonType(facilityItemVO.getApplicationReason());
        
        FacilityDetails facilityDetails = FacilityDetails.builder()
                .name(facilityItemVO.getName())
                .isCoveredByUkets(facilityItemVO.getUketsId() != null)
                .uketsId(facilityItemVO.getUketsId())
                .applicationReason(applicationReason)
                .previousFacilityId(ApplicationReasonType.CHANGE_OF_OWNERSHIP.equals(applicationReason) ? MigrationUtil.convertLegacyToCcaBusinessId(facilityItemVO.getPreviousFacilityId()) : null)
                .facilityAddress(FacilityAddressDTO.builder()
                        .line1(facilityItemVO.getFacilityAddress().getLine1())
                        .line2(facilityItemVO.getFacilityAddress().getLine2())
                        .city(facilityItemVO.getFacilityAddress().getCity())
                        .county(facilityItemVO.getFacilityAddress().getCounty())
                        .postcode(facilityItemVO.getFacilityAddress().getPostcode())
                        .country(resolveCountryCode(facilityItemVO.getFacilityAddress().getCountry(), legacyCountries))
                        .build())
                .build();
        
        PhoneNumberDTO phoneNumberDTO = PhoneNumberDTO.builder()
                .countryCode(StringUtils.isBlank(facilityItemVO.getPhoneNumber()) ? null : "44")
                .number(facilityItemVO.getPhoneNumber())
                .build();
        
        TargetUnitAccountContactDTO facilityContact = TargetUnitAccountContactDTO.builder()
                .email(facilityItemVO.getEmail())
                .firstName(facilityItemVO.getFirstName())
                .lastName(facilityItemVO.getLastName())
                .jobTitle(facilityItemVO.getJobTitle())
                .address(AccountAddressDTO.builder()
                        .line1(facilityItemVO.getAddress().getLine1())
                        .line2(facilityItemVO.getAddress().getLine2())
                        .city(facilityItemVO.getAddress().getCity())
                        .county(facilityItemVO.getAddress().getCounty())
                        .postcode(facilityItemVO.getAddress().getPostcode())
                        .country(resolveCountryCode(facilityItemVO.getAddress().getCountry(), legacyCountries))
                        .build())
                .phoneNumber(phoneNumberDTO)
                .build();
        
        AgreementType agreementType = MigrationUtil.getAgreementType(facilityItemVO.getAgreementType());
                
        EligibilityDetailsAndAuthorisation eligibilityDetailsAndAuthorisation = EligibilityDetailsAndAuthorisation.builder()
                .isConnectedToExistingFacility(StringUtils.isNotBlank(facilityItemVO.getAdjacentFacilityId()))
                .adjacentFacilityId(MigrationUtil.convertLegacyToCcaBusinessId(facilityItemVO.getAdjacentFacilityId()))
                .agreementType(agreementType)
                .erpAuthorisationExists(AgreementType.ENVIRONMENTAL_PERMITTING_REGULATIONS.equals(agreementType) ? facilityItemVO.getErpAuthorisationExists() : null)
                .authorisationNumber(MigrationUtil.cleanString(facilityItemVO.getAuthorisationNumber()))
                .regulatorName(FacilitiesUtil.getRegulatorNameType(facilityItemVO.getRegulatorName()))
                .build();
        
        FacilityExtent facilityExtent = FacilityExtent.builder()
                .areActivitiesClaimed(Boolean.FALSE)
                .build();
                
        Apply70Rule apply70Rule = Apply70Rule.builder()
                .energyConsumed(FacilitiesUtil.getEnergyConsumed(facilityItemVO))
                .energyConsumedProvision(FacilitiesUtil.getEnergyConsumedProvision(facilityItemVO))
                .energyConsumedEligible(FacilitiesUtil.calcEnergyConsumedEligible(facilityItemVO))
                //Not provided in legacy system
                .startDate(null)
                .build();
        
        return FacilityItem.builder()
                .facilityId(MigrationUtil.convertLegacyToCcaBusinessId(facilityItemVO.getFacilityBusinessId()))
                .facilityDetails(facilityDetails)
                .facilityContact(facilityContact)
                .eligibilityDetailsAndAuthorisation(eligibilityDetailsAndAuthorisation)
                .facilityExtent(facilityExtent)
                .apply70Rule(apply70Rule)
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
}
