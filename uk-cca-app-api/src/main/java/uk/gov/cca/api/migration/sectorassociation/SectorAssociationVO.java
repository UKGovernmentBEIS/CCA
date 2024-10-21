package uk.gov.cca.api.migration.sectorassociation;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorAssociationVO {

    private String competentAuthority;

    private Long originalSectorId;
    private String commonName;
    private String acronym;
    private String legalName;
    private String energyIntensiveOrEPR;

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

}
