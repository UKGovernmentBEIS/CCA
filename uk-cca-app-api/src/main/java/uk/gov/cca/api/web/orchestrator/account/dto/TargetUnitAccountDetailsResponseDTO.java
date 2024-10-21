package uk.gov.cca.api.web.orchestrator.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDetailsDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TargetUnitAccountDetailsResponseDTO {

    private TargetUnitAccountDetailsDTO targetUnitAccountDetails;
    private SubsectorAssociationDTO subsectorAssociation;
    private UnderlyingAgreementDetailsDTO underlyingAgreementDetails;
}
