package uk.gov.cca.api.web.orchestrator.facility.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;

import java.time.LocalDate;


@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class FacilitySearchResultExtendedDTO {

	private Long id;
	
    private String facilityBusinessId;

    private String siteName;

    private LocalDate schemeExitDate;

    private FacilityDataStatus status;

    private FacilityCertificationStatus certificationStatus;

	private Boolean auditRequired;
}
