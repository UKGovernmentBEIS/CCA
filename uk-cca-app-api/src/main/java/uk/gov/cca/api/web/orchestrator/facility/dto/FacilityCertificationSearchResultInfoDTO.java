package uk.gov.cca.api.web.orchestrator.facility.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;

import java.time.LocalDate;


@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class FacilityCertificationSearchResultInfoDTO {

    private String id;

    private String siteName;

    private LocalDate schemeExitDate;

    private FacilityDataStatus status;

    private FacilityCertificationStatus certificationStatus;
}
