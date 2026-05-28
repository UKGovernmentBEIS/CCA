package uk.gov.cca.api.migration.createsector.common;

import java.time.LocalDate;

import uk.gov.cca.api.common.domain.AgreementType;

public interface SectorAssociationSource {

	String getAcronym();
    String getCommonName();
    String getLegalName();
    AgreementType getEnergyIntensiveOrEPR();
	
	String getLine1();
    String getLine2();
    String getCity();
    String getCounty();
    String getPostcode();
    
    String getSectorContactTitle();
    String getSectorContactFirstName();
    String getSectorContactLastName();
    String getSectorContactJobTitle();
    String getSectorContactOrganisationName();
    String getSectorContactAddressLine1();
    String getSectorContactAddressLine2();
    String getSectorContactAddressCity();
    String getSectorContactAddressCounty();
    String getSectorContactAddressPostcode();
    String getSectorContactPhoneNumber();
    String getSectorContactEmail();
    
    LocalDate getUmaDate();
    String getSectorDefinition();
    
}
