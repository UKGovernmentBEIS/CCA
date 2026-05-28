package uk.gov.cca.api.migration.createsector.cca2;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.AgreementType;
import uk.gov.cca.api.migration.createsector.common.SectorAssociationSource;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAssociationVO implements SectorAssociationSource {

    private String competentAuthority;

    private Long originalSectorId;
    private String commonName;
    private String acronym;
    private String legalName;
    private AgreementType energyIntensiveOrEPR;

    private String line1;
    private String line2;
    private String city;
    private String county;
    private String postcode;

    // Main Contact
    private String sectorContactTitle;
    private String sectorContactFirstName;
    private String sectorContactLastName;
    private String sectorContactAddressLine1;
    private String sectorContactAddressLine2;
    private String sectorContactAddressCity;
    private String sectorContactAddressCounty;
    private String sectorContactAddressPostcode;
    private String sectorContactPhoneNumber;
    private String sectorContactEmail;

    // Scheme
    private LocalDate umaDate;
    private String sectorDefinition;
    private TargetSetVO targetSet;
    private int subsectorsCounter;
    
	@Override
	public String getSectorContactOrganisationName() {
		return null;
	}

	@Override
	public String getSectorContactJobTitle() {
		return null;
	}

}
